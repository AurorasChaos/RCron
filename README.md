# RCron

![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/YourUsername/RCron?sort=semver)
![GitHub issues](https://img.shields.io/github/issues/YourUsername/RCron)
![License](https://img.shields.io/github/license/YourUsername/RCron)

A lightweight Velocity 1.21.4 plugin that lets you schedule and manage cron‑formatted tasks to run arbitrary console commands on the proxy or any attached backend server.

---

## 🔧 Features

- **Cron Scheduling**: Use Quartz-style cron expressions to schedule commands.
- **Proxy & Backend Targets**: Run commands either on the proxy console or on a specific connected server via `--server <name>`.
- **Dynamic Management**: Add, remove, list, and reload jobs at runtime without restarting Velocity.
- **Persistent Storage**: All jobs are stored in `config.yml` and survive proxy restarts.
- **Simple Configuration**: Human-friendly YAML configuration and intuitive `/rcron` commands.

## ⚙️ Prerequisites

- Java 17+
- Velocity 1.21.4 (or compatible)
- Maven 3.6+

## 🚀 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/YourUsername/RCron.git
   cd RCron
   ```

2. **Build the plugin**
   ```bash
   mvn clean package
   ```

3. **Deploy**
   - Copy the generated `target/rccron-1.0.0.jar` to your Velocity `plugins/` folder.
   - Start or reload your proxy.

## 📝 Configuration

After first run, a `config.yml` will be created in `plugins/rccron/`. Example structure:

```yaml
jobs:
  - id: "d290f1ee-6c54-4b01-90e6-d701748f0851"
    cron: "0 0/5 * * * ?"
    command: "say Hello every 5 minutes"
    server: null          # "lobby" to target a backend server
```

- **id**: Unique UUID of the job (auto‑generated).  
- **cron**: Quartz cron expression (see [Quartz docs](https://www.quartz-scheduler.org/) for syntax).  
- **command**: The console command to run (no leading `/`).  
- **server**: (Optional) Target server name. If `null`, runs on proxy console.

## 💻 Usage

Use the `/rcron` command in the proxy console or chat (with permission):

| Subcommand                           | Description                                                |
| ------------------------------------ | ---------------------------------------------------------- |
| `/rcron add [--server <name>] <cron> <command...>` | Schedule and persist a new job.              |
| `/rcron remove <jobId>`              | Unschedule and delete a job by its UUID.                   |
| `/rcron list`                        | List all configured jobs with details.                     |
| `/rcron reload`                      | Reload `config.yml`, unscheduling and re-scheduling jobs. |

### Examples

- **Every 10 minutes on proxy**
  ```
  /rcron add 0 0/10 * * * ? say "Proxy heartbeat"
  ```

- **Daily at 09:30 on the `lobby` server**
  ```
  /rcron add --server lobby 0 30 9 * * ? say "Good morning players!"
  ```

- **List jobs**
  ```
  /rcron list
  ```

- **Remove a job**
  ```
  /rcron remove d290f1ee-6c54-4b01-90e6-d701748f0851
  ```

## 🔄 Reloading

If you manually edit `config.yml`, run:
```
/rcron reload
```
to apply changes without restarting the proxy.

## 📦 Maven Repository

The plugin uses dependencies from PaperMC’s Maven repository. Ensure your `pom.xml` includes:

```xml
<repositories>
  <repository>
    <id>papermc</id>
    <url>https://repo.papermc.io/repository/maven-public/</url>
  </repository>
</repositories>
```

## 🤝 Contributing

1. Fork the repo.  
2. Create a feature branch (`git checkout -b feature/YourFeature`).  
3. Commit your changes.  
4. Open a pull request.

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

*Crafted with :heart: for Velocity proxy users.*