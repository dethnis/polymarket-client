# ⚠️ Disclaimer

**This is NOT an official Polymarket Java library.** This project is an independent and is not
affiliated with, endorsed by, or maintained by Polymarket.

Use at your own risk. The author is not responsible for any financial losses or issues arising from the use of this library.

# Polymarket Java Client

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org)

A Java client library for interacting with the [Polymarket](https://polymarket.com) prediction market platform via
their CLOB (Central Limit Order Book) API.

Made with help from [Polymarket Kotlin Client](https://github.com/kolchanov-dev/polymarket-client).
Many thanks to [Vadim Kolchanov](https://github.com/kolchanov-dev).

Did some refactoring and added some new request, responses (new market, order book, tick size change, market resolved, last trade, best bid ask, ...).

REady for use with spring boot

---

## 📑 Contents

- [📦 Installation](#-installation)
- [🛠️ Requirements](#️-requirements)
- [📄 License](#-license)
- [🤝 Contributing](#-contributing)

---

## 📦 Installation

### Maven

```xml

<dependency>
    <groupId>dev.polymarket</groupId>
    <artifactId>polymarket-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

---

## 🛠️ Requirements

- **Java 25** or higher

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---