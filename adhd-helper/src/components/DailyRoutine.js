import React, { useState, useEffect } from 'react';

function DailyRoutine() {
  const defaultRoutines = [
    { id: 1, time: '07:00', title: 'Morning Wake Up', description: 'Start your day!', completed: false },
    { id: 2, time: '07:30', title: 'Breakfast', description: 'Eat a healthy breakfast', completed: false },
    { id: 3, time: '08:00', title: 'Morning Hygiene', description: 'Brush teeth, shower, get dressed', completed: false },
    { id: 4, time: '12:00', title: 'Lunch Break', description: 'Take a break and eat lunch', completed: false },
    { id: 5, time: '18:00', title: 'Dinner Time', description: 'Prepare and eat dinner', completed: false },
    { id: 6, time: '21:00', title: 'Evening Wind Down', description: 'Relax and prepare for bed', completed: false },
    { id: 7, time: '22:00', title: 'Bedtime Routine', description: 'Brush teeth, get ready for bed', completed: false }
  ];

  const [routines, setRoutines] = useState([]);
  const [newRoutine, setNewRoutine] = useState({
    time: '',
    title: '',
    description: ''
  });

  // Load routines from localStorage on mount
  useEffect(() => {
    const savedRoutines = localStorage.getItem('adhd-routines');
    if (savedRoutines) {
      setRoutines(JSON.parse(savedRoutines));
    } else {
      setRoutines(defaultRoutines);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Save routines to localStorage whenever they change
  useEffect(() => {
    if (routines.length > 0) {
      localStorage.setItem('adhd-routines', JSON.stringify(routines));
    }
  }, [routines]);

  // Reset completed status at midnight
  useEffect(() => {
    const checkMidnight = setInterval(() => {
      const now = new Date();
      if (now.getHours() === 0 && now.getMinutes() === 0) {
        setRoutines(routines.map(r => ({ ...r, completed: false })));
      }
    }, 60000); // Check every minute

    return () => clearInterval(checkMidnight);
  }, [routines]);

  const addRoutine = (e) => {
    e.preventDefault();
    if (newRoutine.time && newRoutine.title) {
      const routine = {
        id: Date.now(),
        ...newRoutine,
        completed: false
      };
      setRoutines([...routines, routine].sort((a, b) => a.time.localeCompare(b.time)));
      setNewRoutine({ time: '', title: '', description: '' });
    }
  };

  const toggleRoutine = (id) => {
    setRoutines(routines.map(routine =>
      routine.id === id ? { ...routine, completed: !routine.completed } : routine
    ));
  };

  const deleteRoutine = (id) => {
    setRoutines(routines.filter(routine => routine.id !== id));
  };

  const resetDay = () => {
    setRoutines(routines.map(r => ({ ...r, completed: false })));
  };

  const getCurrentTimeBlock = () => {
    const now = new Date();
    const currentTime = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
    return currentTime;
  };

  const isUpcoming = (time) => {
    const currentTime = getCurrentTimeBlock();
    return time > currentTime;
  };

  const completedCount = routines.filter(r => r.completed).length;
  const totalCount = routines.length;
  const completionPercentage = totalCount > 0 ? Math.round((completedCount / totalCount) * 100) : 0;

  return (
    <div className="component-container">
      <h2 className="component-title">
        <span>📅</span> Daily Routines
      </h2>
      <p className="component-description">
        Visual schedules help you know what to expect throughout the day. Check off tasks as you complete them!
      </p>

      <div style={{ 
        marginBottom: '30px', 
        padding: '20px', 
        background: '#f0f9ff', 
        borderRadius: '10px' 
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
          <h3 style={{ color: '#667eea' }}>Today's Progress</h3>
          <button onClick={resetDay} className="btn btn-secondary" style={{ padding: '8px 16px' }}>
            🔄 Reset Day
          </button>
        </div>
        <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#667eea', marginBottom: '10px' }}>
          {completedCount} / {totalCount} tasks completed ({completionPercentage}%)
        </div>
        <div style={{ 
          width: '100%', 
          height: '20px', 
          background: '#e0e0e0', 
          borderRadius: '10px',
          overflow: 'hidden'
        }}>
          <div style={{
            width: `${completionPercentage}%`,
            height: '100%',
            background: 'linear-gradient(90deg, #667eea 0%, #764ba2 100%)',
            transition: 'width 0.5s ease'
          }} />
        </div>
      </div>

      <div style={{ marginBottom: '30px' }}>
        <h3 style={{ marginBottom: '15px', color: '#667eea' }}>Your Schedule</h3>
        {routines.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon">🕐</div>
            <p className="empty-state-text">No routines yet. Add your first routine below!</p>
          </div>
        ) : (
          <ul className="item-list">
            {routines.map(routine => (
              <li 
                key={routine.id} 
                className="item-card"
                style={{ 
                  borderLeft: `5px solid ${routine.completed ? '#10b981' : isUpcoming(routine.time) ? '#667eea' : '#fbbf24'}`,
                  opacity: routine.completed ? 0.7 : 1
                }}
              >
                <div className="item-header">
                  <label className="checkbox-label">
                    <input
                      type="checkbox"
                      checked={routine.completed}
                      onChange={() => toggleRoutine(routine.id)}
                    />
                    <div>
                      <span className="routine-time">{routine.time}</span>
                      <span className={routine.completed ? 'completed-task' : ''}>
                        <strong>{routine.title}</strong>
                      </span>
                    </div>
                  </label>
                  <button 
                    className="icon-btn" 
                    onClick={() => deleteRoutine(routine.id)}
                    title="Delete routine"
                  >
                    🗑️
                  </button>
                </div>
                {routine.description && (
                  <p className="routine-description" style={{ marginLeft: '30px' }}>
                    {routine.description}
                  </p>
                )}
              </li>
            ))}
          </ul>
        )}
      </div>

      <div style={{ 
        padding: '20px', 
        background: '#f9fafb', 
        borderRadius: '10px' 
      }}>
        <h3 style={{ marginBottom: '15px', color: '#667eea' }}>➕ Add New Routine</h3>
        <form onSubmit={addRoutine}>
          <div className="input-group">
            <label htmlFor="routine-time">Time *</label>
            <input
              id="routine-time"
              type="time"
              value={newRoutine.time}
              onChange={(e) => setNewRoutine({ ...newRoutine, time: e.target.value })}
              required
            />
          </div>
          <div className="input-group">
            <label htmlFor="routine-title">Activity *</label>
            <input
              id="routine-title"
              type="text"
              value={newRoutine.title}
              onChange={(e) => setNewRoutine({ ...newRoutine, title: e.target.value })}
              placeholder="e.g., Take medication"
              required
            />
          </div>
          <div className="input-group">
            <label htmlFor="routine-description">Description (optional)</label>
            <input
              id="routine-description"
              type="text"
              value={newRoutine.description}
              onChange={(e) => setNewRoutine({ ...newRoutine, description: e.target.value })}
              placeholder="Add any helpful details..."
            />
          </div>
          <button type="submit" className="btn btn-primary">
            ➕ Add Routine
          </button>
        </form>
      </div>
    </div>
  );
}

export default DailyRoutine;
