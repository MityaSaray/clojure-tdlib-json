# tdlib-json-clojure-wrapper

A Clojure library designed to be a wrapper around tdlib json client using JNA.

There are installation instruction for your platform and general usage there https://github.com/tdlib/td

You will need to create a config.json file in root directory.

{
  "@type": "setTdlibParameters",
  "parameters": {
    "api_id": your_api_id,
    "api_hash": your_api_hash,
    "application_version": "0.1",
    "system_version": "Ubuntu 18.04",
    "system_language_code": "en",
    "device_model": "PC",
    "database_directory": "tg-db"
  }
}

You will need to position build folder of tdlib inside tg_connector. You can look at tdlib_install.sh for example.
Or you can change path in TgClient dir.

During first launch program will ask you phone and code in terminal.

Commands are "lein buildTg" - to build telegram client from source (may take a while).
"lein run" - to launch application.
