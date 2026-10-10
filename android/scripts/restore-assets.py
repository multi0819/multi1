"""Restore public development signing material before a reproducible Gradle build."""
import base64
from pathlib import Path
root = Path(__file__).resolve().parents[1]
source = root / 'signing/travel.jks.b64'
source.with_suffix('').write_bytes(base64.b64decode(source.read_text()))
