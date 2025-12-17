import React, { useState, useEffect } from 'react';

function TaskManager() {
  const [tasks, setTasks] = useState([]);
  const [newTask, setNewTask] = useState('');
  const [taskDetails, setTaskDetails] = useState('');

  // Load tasks from localStorage on mount
  useEffect(() => {
    const savedTasks = localStorage.getItem('adhd-tasks');
    if (savedTasks) {
      setTasks(JSON.parse(savedTasks));
    }
  }, []);

  // Save tasks to localStorage whenever they change
  useEffect(() => {
    localStorage.setItem('adhd-tasks', JSON.stringify(tasks));
  }, [tasks]);

  const addTask = (e) => {
    e.preventDefault();
    if (newTask.trim()) {
      const task = {
        id: Date.now(),
        title: newTask,
        details: taskDetails,
        completed: false,
        subtasks: [],
        createdAt: new Date().toISOString()
      };
      setTasks([...tasks, task]);
      setNewTask('');
      setTaskDetails('');
    }
  };

  const toggleTask = (id) => {
    setTasks(tasks.map(task => 
      task.id === id ? { ...task, completed: !task.completed } : task
    ));
  };

  const deleteTask = (id) => {
    setTasks(tasks.filter(task => task.id !== id));
  };

  const addSubtask = (taskId, subtaskTitle) => {
    setTasks(tasks.map(task => {
      if (task.id === taskId) {
        return {
          ...task,
          subtasks: [...task.subtasks, { id: Date.now(), title: subtaskTitle, completed: false }]
        };
      }
      return task;
    }));
  };

  const toggleSubtask = (taskId, subtaskId) => {
    setTasks(tasks.map(task => {
      if (task.id === taskId) {
        return {
          ...task,
          subtasks: task.subtasks.map(st => 
            st.id === subtaskId ? { ...st, completed: !st.completed } : st
          )
        };
      }
      return task;
    }));
  };

  const incompleteTasks = tasks.filter(t => !t.completed);
  const completedTasks = tasks.filter(t => t.completed);

  return (
    <div className="component-container">
      <h2 className="component-title">
        <span>✓</span> Task Manager
      </h2>
      <p className="component-description">
        Break down tasks into smaller, manageable steps. Check them off as you complete them!
      </p>

      <form onSubmit={addTask} style={{ marginBottom: '30px' }}>
        <div className="input-group">
          <label htmlFor="task-title">Task Title *</label>
          <input
            id="task-title"
            type="text"
            value={newTask}
            onChange={(e) => setNewTask(e.target.value)}
            placeholder="e.g., Clean my room"
            required
          />
        </div>
        <div className="input-group">
          <label htmlFor="task-details">Task Details (optional)</label>
          <textarea
            id="task-details"
            value={taskDetails}
            onChange={(e) => setTaskDetails(e.target.value)}
            placeholder="Add any additional notes or steps here..."
            rows="3"
          />
        </div>
        <button type="submit" className="btn btn-primary">
          ➕ Add Task
        </button>
      </form>

      <div>
        <h3 style={{ marginBottom: '15px', color: '#667eea' }}>
          Active Tasks ({incompleteTasks.length})
        </h3>
        {incompleteTasks.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon">🎉</div>
            <p className="empty-state-text">No active tasks! Add one above to get started.</p>
          </div>
        ) : (
          <ul className="item-list">
            {incompleteTasks.map(task => (
              <TaskItem 
                key={task.id} 
                task={task} 
                onToggle={toggleTask}
                onDelete={deleteTask}
                onAddSubtask={addSubtask}
                onToggleSubtask={toggleSubtask}
              />
            ))}
          </ul>
        )}
      </div>

      {completedTasks.length > 0 && (
        <div style={{ marginTop: '40px' }}>
          <h3 style={{ marginBottom: '15px', color: '#10b981' }}>
            ✓ Completed Tasks ({completedTasks.length})
          </h3>
          <ul className="item-list">
            {completedTasks.map(task => (
              <TaskItem 
                key={task.id} 
                task={task} 
                onToggle={toggleTask}
                onDelete={deleteTask}
                onAddSubtask={addSubtask}
                onToggleSubtask={toggleSubtask}
              />
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

function TaskItem({ task, onToggle, onDelete, onAddSubtask, onToggleSubtask }) {
  const [showSubtaskInput, setShowSubtaskInput] = useState(false);
  const [newSubtask, setNewSubtask] = useState('');

  const handleAddSubtask = (e) => {
    e.preventDefault();
    if (newSubtask.trim()) {
      onAddSubtask(task.id, newSubtask);
      setNewSubtask('');
      setShowSubtaskInput(false);
    }
  };

  return (
    <li className="item-card">
      <div className="item-header">
        <label className="checkbox-label">
          <input
            type="checkbox"
            checked={task.completed}
            onChange={() => onToggle(task.id)}
          />
          <span className={task.completed ? 'completed-task' : ''}>
            <strong>{task.title}</strong>
          </span>
        </label>
        <div className="item-actions">
          <button 
            className="icon-btn" 
            onClick={() => setShowSubtaskInput(!showSubtaskInput)}
            title="Add subtask"
          >
            ➕
          </button>
          <button 
            className="icon-btn" 
            onClick={() => onDelete(task.id)}
            title="Delete task"
          >
            🗑️
          </button>
        </div>
      </div>
      
      {task.details && (
        <p style={{ color: '#666', fontSize: '0.9rem', marginTop: '8px' }}>
          {task.details}
        </p>
      )}

      {task.subtasks.length > 0 && (
        <ul style={{ marginTop: '10px', marginLeft: '30px', listStyle: 'none' }}>
          {task.subtasks.map(subtask => (
            <li key={subtask.id} style={{ marginBottom: '5px' }}>
              <label className="checkbox-label" style={{ fontSize: '0.95rem' }}>
                <input
                  type="checkbox"
                  checked={subtask.completed}
                  onChange={() => onToggleSubtask(task.id, subtask.id)}
                />
                <span className={subtask.completed ? 'completed-task' : ''}>
                  {subtask.title}
                </span>
              </label>
            </li>
          ))}
        </ul>
      )}

      {showSubtaskInput && (
        <form onSubmit={handleAddSubtask} style={{ marginTop: '10px' }}>
          <div style={{ display: 'flex', gap: '8px' }}>
            <input
              type="text"
              value={newSubtask}
              onChange={(e) => setNewSubtask(e.target.value)}
              placeholder="Add a subtask..."
              style={{ flex: 1, padding: '8px', border: '1px solid #ddd', borderRadius: '5px' }}
              autoFocus
            />
            <button type="submit" className="btn btn-secondary" style={{ padding: '8px 16px' }}>
              Add
            </button>
          </div>
        </form>
      )}
    </li>
  );
}

export default TaskManager;
