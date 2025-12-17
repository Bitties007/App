import React, { useState, useEffect } from 'react';

function FocusTimer() {
  const [minutes, setMinutes] = useState(25);
  const [seconds, setSeconds] = useState(0);
  const [isActive, setIsActive] = useState(false);
  const [mode, setMode] = useState('focus'); // 'focus', 'shortBreak', 'longBreak'
  const [sessionCount, setSessionCount] = useState(0);

  const timerModes = {
    focus: { duration: 25, label: 'Focus Time', emoji: '🎯' },
    shortBreak: { duration: 5, label: 'Short Break', emoji: '☕' },
    longBreak: { duration: 15, label: 'Long Break', emoji: '🌟' }
  };

  const playNotification = () => {
    // Play a simple beep sound using Web Audio API
    const audioContext = new (window.AudioContext || window.webkitAudioContext)();
    const oscillator = audioContext.createOscillator();
    const gainNode = audioContext.createGain();
    
    oscillator.connect(gainNode);
    gainNode.connect(audioContext.destination);
    
    oscillator.frequency.value = 800;
    oscillator.type = 'sine';
    
    gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
    gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5);
    
    oscillator.start(audioContext.currentTime);
    oscillator.stop(audioContext.currentTime + 0.5);
  };

  const handleTimerComplete = React.useCallback(() => {
    setIsActive(false);
    
    if (mode === 'focus') {
      const newCount = sessionCount + 1;
      setSessionCount(newCount);
      
      // After 4 focus sessions, suggest a long break
      if (newCount % 4 === 0) {
        setMode('longBreak');
        setMinutes(timerModes.longBreak.duration);
      } else {
        setMode('shortBreak');
        setMinutes(timerModes.shortBreak.duration);
      }
    } else {
      setMode('focus');
      setMinutes(timerModes.focus.duration);
    }
    setSeconds(0);
  }, [mode, sessionCount, timerModes.focus.duration, timerModes.longBreak.duration, timerModes.shortBreak.duration]);

  useEffect(() => {
    let interval = null;

    if (isActive) {
      interval = setInterval(() => {
        if (seconds === 0) {
          if (minutes === 0) {
            // Timer completed
            playNotification();
            handleTimerComplete();
          } else {
            setMinutes(minutes - 1);
            setSeconds(59);
          }
        } else {
          setSeconds(seconds - 1);
        }
      }, 1000);
    } else {
      clearInterval(interval);
    }

    return () => clearInterval(interval);
  }, [isActive, minutes, seconds, handleTimerComplete]);

  const toggleTimer = () => {
    setIsActive(!isActive);
  };

  const resetTimer = () => {
    setIsActive(false);
    setMinutes(timerModes[mode].duration);
    setSeconds(0);
  };

  const switchMode = (newMode) => {
    setIsActive(false);
    setMode(newMode);
    setMinutes(timerModes[newMode].duration);
    setSeconds(0);
  };

  const formatTime = (mins, secs) => {
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const progress = ((timerModes[mode].duration * 60 - (minutes * 60 + seconds)) / (timerModes[mode].duration * 60)) * 100;

  return (
    <div className="component-container">
      <h2 className="component-title">
        <span>⏱️</span> Focus Timer
      </h2>
      <p className="component-description">
        Use the Pomodoro Technique to maintain focus. Work for 25 minutes, then take a break!
      </p>

      <div style={{ textAlign: 'center', marginTop: '30px' }}>
        <div style={{ marginBottom: '20px' }}>
          <h3 style={{ fontSize: '1.5rem', color: '#667eea', marginBottom: '10px' }}>
            {timerModes[mode].emoji} {timerModes[mode].label}
          </h3>
          <p style={{ color: '#666' }}>Focus Sessions Completed: {sessionCount}</p>
        </div>

        <div 
          className="timer-display"
          style={{ 
            color: mode === 'focus' ? '#667eea' : '#10b981',
            position: 'relative'
          }}
        >
          {formatTime(minutes, seconds)}
        </div>

        <div style={{ 
          width: '100%', 
          height: '10px', 
          background: '#e0e0e0', 
          borderRadius: '5px',
          marginBottom: '30px',
          overflow: 'hidden'
        }}>
          <div style={{
            width: `${progress}%`,
            height: '100%',
            background: mode === 'focus' ? '#667eea' : '#10b981',
            transition: 'width 1s linear'
          }} />
        </div>

        <div className="timer-controls">
          <button 
            onClick={toggleTimer}
            className={`btn ${isActive ? 'btn-danger' : 'btn-success'}`}
          >
            {isActive ? '⏸️ Pause' : '▶️ Start'}
          </button>
          <button 
            onClick={resetTimer}
            className="btn btn-secondary"
          >
            🔄 Reset
          </button>
        </div>

        <div style={{ marginTop: '40px' }}>
          <h4 style={{ marginBottom: '15px', color: '#666' }}>Switch Mode:</h4>
          <div style={{ display: 'flex', gap: '10px', justifyContent: 'center', flexWrap: 'wrap' }}>
            <button
              onClick={() => switchMode('focus')}
              className={`btn ${mode === 'focus' ? 'btn-primary' : 'btn-secondary'}`}
            >
              🎯 Focus (25 min)
            </button>
            <button
              onClick={() => switchMode('shortBreak')}
              className={`btn ${mode === 'shortBreak' ? 'btn-primary' : 'btn-secondary'}`}
            >
              ☕ Short Break (5 min)
            </button>
            <button
              onClick={() => switchMode('longBreak')}
              className={`btn ${mode === 'longBreak' ? 'btn-primary' : 'btn-secondary'}`}
            >
              🌟 Long Break (15 min)
            </button>
          </div>
        </div>

        <div style={{ 
          marginTop: '40px', 
          padding: '20px', 
          background: '#f0f9ff', 
          borderRadius: '10px',
          textAlign: 'left'
        }}>
          <h4 style={{ marginBottom: '10px', color: '#667eea' }}>💡 How to Use:</h4>
          <ol style={{ lineHeight: '1.8', color: '#666', paddingLeft: '20px' }}>
            <li>Choose a task you want to work on</li>
            <li>Set the timer to 25 minutes (Focus Time)</li>
            <li>Work on the task until the timer rings</li>
            <li>Take a short 5-minute break</li>
            <li>After 4 focus sessions, take a longer 15-minute break</li>
          </ol>
        </div>
      </div>
    </div>
  );
}

export default FocusTimer;
