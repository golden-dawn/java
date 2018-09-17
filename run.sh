#!/bin/bash
cd output
java -cp .:postgresql-42.2.2.jar $@
cd ..
