#!/bin/bash -e

for i in {1..1000}
do
    curl -kI https://localhost:8443/products
done
