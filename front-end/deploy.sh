#!/usr/bin/env bash

# Параметры подключения
SERVER_USER="begzh"
SERVER_HOST="2.tcp.eu.ngrok.io"
SERVER_PORT="17853"
SSH_KEY="C:\\Users\\Beka\\.ssh\\id_rsa"
REMOTE_PATH="/app/main-ui"
LOCAL_BUILD_PATH="dist"

echo "Building Vue.js frontend..."
npm install && npm run build || { echo "Build failed"; exit 1; }

echo "Removing old files from server..."
ssh -i "$SSH_KEY" -p $SERVER_PORT $SERVER_USER@$SERVER_HOST "rm -rf $REMOTE_PATH/*"

echo "Copying files to server..."
scp -i "$SSH_KEY" -P $SERVER_PORT -r "$LOCAL_BUILD_PATH"/* $SERVER_USER@$SERVER_HOST:$REMOTE_PATH

if [ $? -eq 0 ]; then
  echo "Files successfully copied to $REMOTE_PATH on server."
else
  echo "File copy failed!"
  exit 1
fi

echo "Deployment finished. Bye!"
