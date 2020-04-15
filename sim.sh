#!/bin/sh

PREFIX=$(realpath $(dirname "$0"))

PYDK=$(realpath ../pydk)

HOST=$(echo -n ${PYDK}/host)

echo HOST=$HOST

PYTHON=$(echo -n ${HOST}/bin/python3.?)
PIP=$(echo -n ${HOST}/bin/pip3.?)
export LD_LIBRARY_PATH="${HOST}/lib64:${HOST}/lib:$LD_LIBRARY_PATH"

PYTHONPATH=. $PYTHON -u -B -i -m simulator "$@"

