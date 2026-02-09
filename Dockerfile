# syntax=docker/dockerfile:1

# Comments are provided throughout this file to help you get started.
# If you need more help, visit the Dockerfile reference guide at
# https://docs.docker.com/go/dockerfile-reference/

# Want to help us make this template better? Share your feedback here: https://forms.gle/ybq9Krt8jtBL3iCk7

################################################################################
# Pick a base image to serve as the foundation for the other build stages in
# this file.
#
# For illustrative purposes, the following FROM command
# is using the alpine image (see https://hub.docker.com/_/alpine).
# By specifying the "latest" tag, it will also use whatever happens to be the
# most recent version of that image when you build your Dockerfile.
# If reproducibility is important, consider using a versioned tag
# (e.g., alpine:3.17.2) or SHA (e.g., alpine@sha256:c41ab5c992deb4fe7e5da09f67a8804a46bd0592bfdf0b1847dde0e0889d2bff).
FROM clojure:temurin-25-tools-deps-trixie AS base

################################################################################
# Create a stage for building/compiling the application.
#
# Generated Example
####################
# The following commands will leverage the "base" stage above to generate
# a "hello world" script and make it executable, but for a real application, you
# would issue a RUN command for your application's build process to generate the
# executable. For language-specific examples, take a look at the Dockerfiles in
# the Awesome Compose repository: https://github.com/docker/awesome-compose
# FROM base as build
# RUN echo -e '#!/bin/sh\n\
# echo Hello world from $(whoami)! In order to get your application running in a container, take a look at the comments in the Dockerfile to get started.'\
# > /bin/hello.sh
# RUN chmod +x /bin/hello.sh
####################
FROM base AS build

RUN mkdir -p /build
WORKDIR /build

# Install and Cache Clojure Dependencies (add before copying code to keep layer cached when code changes)
# The dependencies are cached in the Docker overlay (layer) and this cache will be used on successive docker builds unless the deps.edn file is change.
# TODO: if build is the WORKDIR does/should it the absolute path be used as the argument to COPY?
COPY deps.edn build.clj /build/
# TODO: use bb task
# 
# clojure cli -P option:
# Use -P before any of the other exec-opts (-A, -X, -M, -T) to do a full deps expansion, download deps, and cache the classpath, but not actually execute the function, tool, main, etc.
RUN clojure -P -X:build

# Copy project files to the builder working directory
COPY ./ /build/
RUN bb ci

################################################################################
# Create a final stage for running your application.
#
# The following commands copy the output from the "build" stage above and tell
# the container runtime to execute it when the image is run. Ideally this stage
# contains the minimal runtime dependencies for the application as to produce
# the smallest image possible. This often means using a different and smaller
# image than the one used for building the application, but for illustrative
# purposes the "base" image is used here.
#
# Generated Example
####################
# FROM base AS final
#
# # Create a non-privileged user that the app will run under.
# # See https://docs.docker.com/go/dockerfile-user-best-practices/
# ARG UID=10001
# RUN adduser \
#     --disabled-password \
#     --gecos "" \
#     --home "/nonexistent" \
#     --shell "/sbin/nologin" \
#     --no-create-home \
#     --uid "${UID}" \
#     appuser
# USER appuser
#
# # Copy the executable from the "build" stage.
# COPY --from=build /bin/hello.sh /bin/
#
# # What the container should run when it is started.
# ENTRYPOINT [ "/bin/hello.sh" ]
####################

FROM eclipse-temurin:25 AS final

# TODO: create non-privileged user that the app will run under (see above)
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser

USER appuser

RUN mkdir /opt/app
COPY --from=build /build/target/net.clojars.jsm13/mynewapp-0.1.0-SNAPSHOT.jar /opt/app/

CMD ["java", "-jar", "/opt/app/mynewapp-0.1.0-SNAPSHOT.jar"]

# TODO: https://hub.docker.com/_/eclipse-temurin#creating-a-jre-using-jlink
