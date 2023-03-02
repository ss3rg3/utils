from pathlib import PurePath

from ruamel.yaml import YAML

from cli.utils import Utils

yaml = YAML(typ='safe')


def fromFile(path: PurePath) -> dict:
    try:
        with open(path, 'r') as yamlFile:
            return yaml.load(yamlFile)
    except Exception as e:
        raise e


def fromString(yamlAsString: str) -> dict:
    try:
        return yaml.load(yamlAsString)
    except Exception as e:
        raise e


def tryToParseFromString(yamlAsString: str, errorMessage: str) -> dict:
    try:
        return yaml.load(yamlAsString)
    except Exception as e:
        Utils.exitWithException(errorMessage, e)


def tryToParseFromFile(path: PurePath, errorMessage: str):
    try:
        return fromFile(path)
    except Exception as e:
        Utils.exitWithException(errorMessage, e)
