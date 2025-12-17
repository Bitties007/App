import React, { useState, useEffect } from 'react';

function ReminderSystem() {
  const [reminders, setReminders] = useState([]);
  const [newReminder, setNewReminder] = useState({
    title: '',
    time: '',
    repeat: 'once'
  });

  const playNotificationSound = () => {
    const audioContext = new (window.AudioContext || window.webkitAudioContext)();
    const oscillator = audioContext.createOscillator();
    const gainNode = audioContext.createGain();
    
    oscillator.connect(gainNode);
    gainNode.connect(audioContext.destination);
    
    oscillator.frequency.value = 800;
    oscillator.type = 'sine';
    
    gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
    gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.3);
    
    oscillator.start(audioContext.currentTime);
    oscillator.stop(audioContext.currentTime + 0.3);
  };

  const showNotification = React.useCallback((reminder) => {
    // Browser notification
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification('⏰ Reminder', {
        body: reminder.title,
        icon: '/favicon.ico'
      });
    }
    
    // Visual alert
    alert(`⏰ Reminder: ${reminder.title}`);
    
    // Play sound
    playNotificationSound();
  }, []);

  // Load reminders from localStorage on mount
  useEffect(() => {
    const savedReminders = localStorage.getItem('adhd-reminders');
    if (savedReminders) {
      setReminders(JSON.parse(savedReminders));
    }
  }, []);

  // Save reminders to localStorage whenever they change
  useEffect(() => {
    localStorage.setItem('adhd-reminders', JSON.stringify(reminders));
  }, [reminders]);

  // Check for due reminders
  useEffect(() => {
    const checkReminders = setInterval(() => {
      const now = new Date();
      const currentTime = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
      
      reminders.forEach(reminder => {
        if (reminder.time === currentTime && !reminder.notified) {
          showNotification(reminder);
          
          // Mark as notified
          setReminders(prevReminders => 
            prevReminders.map(r => 
              r.id === reminder.id ? { ...r, notified: true } : r
            )
          );
        }
      });
    }, 30000); // Check every 30 seconds

    return () => clearInterval(checkReminders);
  }, [reminders, showNotification]);

  // Reset notified status at midnight for repeating reminders
  useEffect(() => {
    const resetNotifications = setInterval(() => {
      const now = new Date();
      if (now.getHours() === 0 && now.getMinutes() === 0) {
        setReminders(prevReminders =>
          prevReminders.map(r => ({ ...r, notified: false }))
        );
      }
    }, 60000);

    return () => clearInterval(resetNotifications);
  }, []);

  const requestNotificationPermission = () => {
    if ('Notification' in window && Notification.permission === 'default') {
      Notification.requestPermission();
    }
  };

  const addReminder = (e) => {
    e.preventDefault();
    if (newReminder.title && newReminder.time) {
      const reminder = {
        id: Date.now(),
        ...newReminder,
        notified: false,
        createdAt: new Date().toISOString()
      };
      setReminders([...reminders, reminder].sort((a, b) => a.time.localeCompare(b.time)));
      setNewReminder({ title: '', time: '', repeat: 'once' });
    }
  };

  const deleteReminder = (id) => {
    setReminders(reminders.filter(reminder => reminder.id !== id));
  };

  const dismissReminder = (id) => {
    setReminders(reminders.map(reminder =>
      reminder.id === id ? { ...reminder, notified: false } : reminder
    ));
  };

  const isUpcoming = (time) => {
    const now = new Date();
    const currentTime = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
    return time > currentTime;
  };

  const notificationStatus = 'Notification' in window ? Notification.permission : 'not-supported';

  return (
    <div className="component-container">
      <h2 className="component-title">
        <span>🔔</span> Reminder System
      </h2>
      <p className="component-description">
        Set reminders for important tasks, medication, or anything you need to remember throughout the day.
      </p>

      {notificationStatus === 'default' && (
        <div style={{ 
          marginBottom: '20px', 
          padding: '15px', 
          background: '#fef3c7', 
          borderRadius: '10px',
          border: '2px solid #fbbf24'
        }}>
          <p style={{ marginBottom: '10px', fontWeight: 'bold' }}>
            🔔 Enable browser notifications for the best experience!
          </p>
          <button 
            onClick={requestNotificationPermission}
            className="btn btn-primary"
            style={{ padding: '8px 16px' }}
          >
            Enable Notifications
          </button>
        </div>
      )}

      {notificationStatus === 'denied' && (
        <div style={{ 
          marginBottom: '20px', 
          padding: '15px', 
          background: '#fee2e2', 
          borderRadius: '10px',
          border: '2px solid #ef4444'
        }}>
          <p>
            ⚠️ Notifications are blocked. Please enable them in your browser settings to receive reminder alerts.
          </p>
        </div>
      )}

      <div style={{ 
        padding: '20px', 
        background: '#f9fafb', 
        borderRadius: '10px',
        marginBottom: '30px'
      }}>
        <h3 style={{ marginBottom: '15px', color: '#667eea' }}>➕ Create New Reminder</h3>
        <form onSubmit={addReminder}>
          <div className="input-group">
            <label htmlFor="reminder-title">What do you need to remember? *</label>
            <input
              id="reminder-title"
              type="text"
              value={newReminder.title}
              onChange={(e) => setNewReminder({ ...newReminder, title: e.target.value })}
              placeholder="e.g., Take medication, Call doctor, etc."
              required
            />
          </div>
          <div className="input-group">
            <label htmlFor="reminder-time">Time *</label>
            <input
              id="reminder-time"
              type="time"
              value={newReminder.time}
              onChange={(e) => setNewReminder({ ...newReminder, time: e.target.value })}
              required
            />
          </div>
          <div className="input-group">
            <label htmlFor="reminder-repeat">Repeat</label>
            <select
              id="reminder-repeat"
              value={newReminder.repeat}
              onChange={(e) => setNewReminder({ ...newReminder, repeat: e.target.value })}
            >
              <option value="once">Once</option>
              <option value="daily">Daily</option>
            </select>
          </div>
          <button type="submit" className="btn btn-primary">
            ➕ Add Reminder
          </button>
        </form>
      </div>

      <div>
        <h3 style={{ marginBottom: '15px', color: '#667eea' }}>
          Your Reminders ({reminders.length})
        </h3>
        
        {reminders.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon">🔔</div>
            <p className="empty-state-text">No reminders set. Create one above!</p>
          </div>
        ) : (
          <ul className="item-list">
            {reminders.map(reminder => (
              <li 
                key={reminder.id} 
                className="item-card"
                style={{ 
                  borderLeft: `5px solid ${reminder.notified ? '#10b981' : isUpcoming(reminder.time) ? '#667eea' : '#fbbf24'}`
                }}
              >
                <div className="item-header">
                  <div>
                    <span className="routine-time">{reminder.time}</span>
                    <strong>{reminder.title}</strong>
                    {reminder.repeat === 'daily' && (
                      <span style={{ 
                        marginLeft: '10px', 
                        padding: '2px 8px', 
                        background: '#e0e7ff', 
                        color: '#667eea',
                        borderRadius: '5px',
                        fontSize: '0.85rem'
                      }}>
                        🔁 Daily
                      </span>
                    )}
                    {reminder.notified && (
                      <span style={{ 
                        marginLeft: '10px', 
                        padding: '2px 8px', 
                        background: '#d1fae5', 
                        color: '#10b981',
                        borderRadius: '5px',
                        fontSize: '0.85rem'
                      }}>
                        ✓ Notified
                      </span>
                    )}
                  </div>
                  <div className="item-actions">
                    {reminder.notified && (
                      <button 
                        className="icon-btn" 
                        onClick={() => dismissReminder(reminder.id)}
                        title="Reset notification"
                      >
                        🔄
                      </button>
                    )}
                    <button 
                      className="icon-btn" 
                      onClick={() => deleteReminder(reminder.id)}
                      title="Delete reminder"
                    >
                      🗑️
                    </button>
                  </div>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>

      <div style={{ 
        marginTop: '30px', 
        padding: '20px', 
        background: '#f0f9ff', 
        borderRadius: '10px' 
      }}>
        <h4 style={{ marginBottom: '10px', color: '#667eea' }}>💡 Tips:</h4>
        <ul style={{ lineHeight: '1.8', color: '#666', paddingLeft: '20px' }}>
          <li>Set medication reminders at the same time every day</li>
          <li>Use reminders for transition times (e.g., "Leave for appointment in 15 minutes")</li>
          <li>Create reminders for self-care activities you might forget</li>
          <li>Keep your phone/browser open to receive notifications</li>
        </ul>
      </div>
    </div>
  );
}

export default ReminderSystem;
