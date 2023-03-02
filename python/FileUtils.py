from pathlib import Path

from cli.utils import Utils


# todo use path
def assertDirExists(path: str, errorMessage: str):
    path = Path(path)
    if not path.exists():
        Utils.exitWithError(f"Directory does not exist, need: {path}\n{errorMessage}")
    if not path.is_dir():
        Utils.exitWithError(f"Path is not a directory, check: {path}\n{errorMessage}")


def loadFileAsString(path: Path, errorMessage: str) -> str:
    with open(path) as file:
        try:
            return file.read().strip()
        except Exception as e:
            Utils.exitWithException(errorMessage, e)


def loadFileAsList(path: Path, errorMessage: str) -> list:
    with open(path) as file:
        try:
            return file.readlines()
        except Exception as e:
            Utils.exitWithException(errorMessage, e)


def loadFileAsStringOrThrow(path: Path) -> str:
    with open(path) as file:
        try:
            return file.read().strip()
        except Exception as e:
            raise e


def writeToFile(path: Path, content: str, errorMessage: str):
    try:
        if not path.parent.exists():
            path.parent.mkdir(parents=True, exist_ok=True)
        with open(path, "w") as file:
            file.write(content)
    except Exception as e:
        Utils.exitWithException(errorMessage, e)


def resolvePath(basePath: Path, pathToAppend: str) -> Path:
    pathToAppend = pathToAppend.strip()
    if pathToAppend.startswith("/"):
        return Path(pathToAppend.strip())
    elif pathToAppend.startswith("~/"):
        return Path(pathToAppend.strip().replace("~/", f"{str(Path.home())}/"))
    elif pathToAppend.startswith("./"):
        return basePath.joinpath(pathToAppend.strip().replace("./", ""))
    else:
        return basePath.joinpath(pathToAppend.strip())


def isSubPathOf(possibleSubPath: Path, possibleParent: Path):
    return str(possibleParent.absolute()).startswith(str(possibleSubPath.absolute()))