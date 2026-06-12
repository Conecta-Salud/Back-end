import pathlib, re, json
root = pathlib.Path('src/main/java/com/itesm/application/usecase')
files = sorted(root.rglob('*.java'))
results = []
for f in files:
    text = f.read_text(encoding='utf-8')
    pkg = re.search(r'package\s+([\w.]+);', text)
    cls = re.search(r'public class\s+(\w+)', text)
    ctor = re.search(r'public\s+\w+\s*\(([^)]*)\)\s*\{', text)
    execute = re.search(r'public\s+[^\{]*execute\s*\(([^)]*)\)', text)
    results.append({
        'file': str(f),
        'package': pkg.group(1) if pkg else None,
        'class': cls.group(1) if cls else None,
        'ctor': ctor.group(1).strip() if ctor else None,
        'execute': execute.group(1).strip() if execute else None,
        'has_throw': 'throw new' in text
    })
print(json.dumps(results, indent=2, ensure_ascii=False))
