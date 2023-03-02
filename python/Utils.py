import click


def exitWithError(errorMessage: str):
    click.secho(f"{errorMessage}", fg="red")
    exit(1)


def exitWithException(errorMessage: str, exception: Exception):
    click.secho(f"{exception}", fg="red")
    click.secho(f"{errorMessage}", fg="red")
    exit(1)


def printInfo(message: str):
    click.secho(f"{message}", fg="yellow")