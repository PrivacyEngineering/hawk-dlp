# Hawk DLP (WIP) - A vendor independent Data Loss Prevention wrapper

This project aims to build a general abstraction above all
major DLP APIs. A core feature is to provide a rest api used to trigger jobs in each underlying DLP
implementation. The project consists of the following modules:

- **hawk-dlp-common** module containing the abstract dlp schema with Jackson JSON mappers
- **hawk-dlp-integration** module containing job abstractions and common spring utilities
- **hawk-dlp-integration-google-cloud-dlp** module containing the schema and endpoint implementation for CDLP
- **hawk-dlp-integration-amazon-macie2** module containing the schema and endpoint implementation for Macie2