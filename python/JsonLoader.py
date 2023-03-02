import json
from pathlib import PurePath

from cli.utils import Utils


def fromFile(path: PurePath) -> dict:
    try:
        with open(path, 'r') as file:
            return json.load(file)
    except Exception as e:
        raise e


# todo delete, use the one with errorMessage
def fromStringOrThrow(jsonAsString: str) -> dict:
    try:
        return json.loads(jsonAsString)
    except Exception as e:
        raise e


def fromString(jsonAsString: str, errorMessage: str) -> dict:
    try:
        return json.loads(jsonAsString)
    except Exception as e:
        Utils.exitWithException(errorMessage, e)
