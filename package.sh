#!/usr/bin/env bash

jpackage --type deb \
    --input target/ \
    --name wabbitj \
    --main-class com.github.rharri.wabbitj.Main \
    --main-jar wabbitj-0.0.1.jar \
    --dest bin/ \
    --app-version 0.0.1 
    
