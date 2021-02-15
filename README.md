# Docker HttpWebServer

A simple file server handling GET and HEAD Requests running in a Docker Container.

## Getting Started

These instructions will cover usage information for the docker container 

### Prerequisities

In order to run this container you'll need docker and docker compose installed.

#### Install Docker
* [Windows](https://docs.docker.com/windows/started)
* [OS X](https://docs.docker.com/mac/started/)
* [Linux](https://docs.docker.com/linux/started/)

#### Install Docker Compose
* [Docker Compose](https://docs.docker.com/compose/install/)

### Usage

1. Add directory/files to project folder
2. Run the Container with `ROOTDIR=[directory] docker-compose up`
3. Explore files by sending requests to `localhost:8080/[path in directory]`

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

