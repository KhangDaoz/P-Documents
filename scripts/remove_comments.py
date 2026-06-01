#!/usr/bin/env python3
"""Remove common comment forms from source files in the repository.

Usage: python scripts/remove_comments.py

This script skips the `target/` and `.git/` directories and writes files back
only when changes are made. It prints a summary of modified files.
"""
import os
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
EXCLUDE_DIRS = {".git", "target", "node_modules"}

EXT_PATTERNS = {
    # C-like languages: Java, JS, CSS, etc.
    ('.java', '.js', '.css', '.c', '.cpp', '.h', '.ts'): [
        (re.compile(r'/\*.*?\*/', re.DOTALL), ''),
        (re.compile(r'//.*?$' , re.MULTILINE), ''),
    ],
    # SQL files: -- line comments and /* */ blocks
    ('.sql', '.psql'): [
        (re.compile(r'/\*.*?\*/', re.DOTALL), ''),
        (re.compile(r'--.*?$' , re.MULTILINE), ''),
    ],
    # XML/HTML: <!-- -->
    ('.xml', '.html', '.xhtml', '.jsp', '.jspx'): [
        (re.compile(r'<!--.*?-->', re.DOTALL), ''),
    ],
    # Shell, properties, YAML, Markdown, plain text: remove lines starting with # or !
    ('.sh', '.properties', '.yml', '.yaml', '.md', '.txt', '.env'): [
        (re.compile(r'(?m)^\s*[#!].*$'), ''),
    ],
    # XML-like also may include xml comments in pom.xml
    ('.pom',): [
        (re.compile(r'<!--.*?-->', re.DOTALL), ''),
    ],
}

def patterns_for_extension(ext: str):
    for exts, pats in EXT_PATTERNS.items():
        if ext in exts:
            return pats
    return None

def should_skip(path: Path):
    for part in path.parts:
        if part in EXCLUDE_DIRS:
            return True
    return False

def process_file(path: Path):
    ext = path.suffix.lower()
    pats = patterns_for_extension(ext)
    if pats is None:
        return False
    try:
        text = path.read_text(encoding='utf-8')
    except Exception:
        return False

    original = text
    for regex, repl in pats:
        text = regex.sub(repl, text)

    # collapse multiple blank lines to a single blank line (keep files tidy)
    text = re.sub(r"\n{3,}", "\n\n", text)

    if text != original:
        path.write_text(text, encoding='utf-8')
        return True
    return False

def main():
    modified = []
    for root, dirs, files in os.walk(ROOT):
        # skip excluded dirs early
        dirs[:] = [d for d in dirs if d not in EXCLUDE_DIRS]
        for f in files:
            p = Path(root) / f
            if should_skip(p):
                continue
            if process_file(p):
                modified.append(str(p.relative_to(ROOT)))

    print(f"Modified {len(modified)} files")
    for m in modified:
        print(m)

if __name__ == '__main__':
    main()
