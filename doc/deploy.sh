#!/usr/bin/env sh

# abort on errors
set -e

# build
yarn build

ossutil cp -rf .vuepress/dist  oss://jpress-doc-site-hk/

cd -