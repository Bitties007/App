import React, { useState } from 'react';
import './App.css';
import TaskManager from './components/TaskManager';
import FocusTimer from './components/FocusTimer';
import DailyRoutine from './components/DailyRoutine';
import ReminderSystem from './components/ReminderSystem';

function App() {
  const [activeTab, setActiveTab] = useState('tasks');

  return (
    <div className="app">
      <header className="app-header">
        <h1>🧠 Executive Function Helper</h1>
        <p className="subtitle">Supporting ADHD/ASD executive functioning</p>
      </header>

      <nav className="tab-navigation">
        <button 
          className={`tab-button ${activeTab === 'tasks' ? 'active' : ''}`}
          onClick={() => setActiveTab('tasks')}
        >
          ✓ Tasks
        </button>
        <button 
          className={`tab-button ${activeTab === 'timer' ? 'active' : ''}`}
          onClick={() => setActiveTab('timer')}
        >
          ⏱️ Focus Timer
        </button>
        <button 
          className={`tab-button ${activeTab === 'routine' ? 'active' : ''}`}
          onClick={() => setActiveTab('routine')}
        >
          📅 Routines
        </button>
        <button 
          className={`tab-button ${activeTab === 'reminders' ? 'active' : ''}`}
          onClick={() => setActiveTab('reminders')}
        >
          🔔 Reminders
        </button>
      </nav>

      <main className="app-content">
        {activeTab === 'tasks' && <TaskManager />}
        {activeTab === 'timer' && <FocusTimer />}
        {activeTab === 'routine' && <DailyRoutine />}
        {activeTab === 'reminders' && <ReminderSystem />}
      </main>

      <footer className="app-footer">
        <p>💙 Designed with neurodivergent needs in mind</p>
      </footer>
    </div>
  );
}

export default App;
