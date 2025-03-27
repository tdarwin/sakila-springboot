#! /bin/bash

for i in $(cat words); do curl http://localhost:8080/api/films/search\?title=${i}; sleep 1; done
for i in $(tac words); do curl http://localhost:8080/api/films/search\?title=${i}; sleep 1; done
