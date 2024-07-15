**Request Tracker**
Request Tracker is an Android application designed to log and track search queries made in web browsers using an Accessibility Service. The app captures search queries, stores them in a local database, and allows users to view and manage their search history.

**Features**
- Accessibility Service: Monitors active browser sessions to capture search queries.
- Local Database: Uses Room for persistent storage of search queries, allowing users to review their history.
- User Interface: A simple and intuitive interface for displaying search queries and managing them.
  
**Configuration Options**
Accessibility Service Settings: The app requires the user to enable the Accessibility Service to function properly. Users will receive prompts if the service is not enabled.

**Dependencies**
The project relies on several external libraries and tools, including:
- AndroidX Libraries: For core Android functionalities and UI components.
- Dagger Hilt: For dependency injection to simplify the management of app components.
- Room: For database management to store search queries locally.
- Lifecycle Libraries: To ensure proper lifecycle management within the app.
  
**Troubleshooting Tips**
- Permission Denied Errors: Ensure you have the correct SSH keys set up for version control.
- Accessibility Service Issues: Verify that the Accessibility Service is enabled in device settings.
- Database Problems: Check for proper implementation of Room components and database migrations.
- App Crashes: Use Logcat to monitor for error messages and resolve issues.
By following these guidelines, users can effectively utilize the Request Tracker app and troubleshoot common issues that may arise during use.
