# 🧠 Executive Function Helper

A web application designed to support individuals with ADHD and ASD by providing tools to help with executive functioning challenges.

## Features

### ✓ Task Manager
- Break down large tasks into smaller, manageable subtasks
- Visual checklist format for easy tracking
- Mark tasks as complete with satisfying checkboxes
- Add detailed notes and context to each task
- Persistent storage using browser localStorage

### ⏱️ Focus Timer (Pomodoro)
- 25-minute focus sessions followed by breaks
- Visual progress indicator
- Audio notifications when timers complete
- Short breaks (5 min) and long breaks (15 min)
- Track completed focus sessions
- Helps maintain attention and prevent burnout

### 📅 Daily Routines
- Visual schedule for daily activities
- Time-based routine tracking
- Progress bar showing daily completion
- Color-coded indicators for past, current, and upcoming tasks
- Customizable routine items
- Automatic reset at midnight

### 🔔 Reminder System
- Set time-based reminders for important tasks
- Browser notifications support
- Daily repeating reminders for recurring tasks
- Visual and audio alerts
- Perfect for medication reminders, appointments, and transitions

## Why This Helps

Executive functioning challenges in ADHD/ASD can include:
- **Task Initiation**: Breaking tasks into steps makes starting easier
- **Time Management**: Visual timers and schedules provide structure
- **Working Memory**: Reminders compensate for forgetfulness
- **Organization**: Clear visual layouts reduce cognitive load
- **Planning**: Routine schedules provide predictability

## Getting Started

### Prerequisites
- Node.js (v14 or higher)
- Modern web browser

### Installation

1. Clone the repository:
```bash
git clone https://github.com/Bitties007/App.git
cd App
```

2. Navigate to the app directory:
```bash
cd adhd-helper
```

3. Install dependencies:
```bash
npm install
```

4. Start the development server:
```bash
npm start
```

5. Open [http://localhost:3000](http://localhost:3000) in your browser

### Building for Production

```bash
npm run build
```

This creates an optimized production build in the `build` folder.

## Design Principles

This app is built with neurodivergent needs in mind:

- **Clear Visual Hierarchy**: Easy to scan and understand at a glance
- **Minimal Distractions**: Clean, focused interface
- **Visual Feedback**: Immediate confirmation of actions
- **Persistent Data**: Everything is saved automatically
- **Customizable**: Add, edit, or remove items as needed
- **Mobile Friendly**: Works on phones, tablets, and desktops

## Technology Stack

- **React**: Modern, component-based UI framework
- **CSS3**: Custom styling for visual clarity
- **LocalStorage**: Client-side data persistence
- **Web Audio API**: Audio notifications
- **Notification API**: Browser notifications

## Browser Support

Works best in modern browsers with support for:
- LocalStorage
- Notifications API
- Web Audio API

Recommended browsers: Chrome, Firefox, Safari, Edge (latest versions)

## Data Privacy

All data is stored locally in your browser. Nothing is sent to external servers. Your tasks, routines, and reminders stay on your device.

## Contributing

Contributions are welcome! If you have ideas for features that would help with executive functioning, please open an issue or submit a pull request.

## License

MIT License - feel free to use and modify for your needs

## Support

If you find this app helpful, consider:
- Sharing it with others who might benefit
- Contributing improvements
- Providing feedback on what works and what doesn't

---

💙 Built with understanding and support for neurodivergent individuals
