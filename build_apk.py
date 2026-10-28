import os
import ssl
import sys
import time
import zipfile
import urllib.request
import subprocess

JDK_DIR = r"C:\Users\Usuario(a) Master\.jdks\temurin-17"
SDK_DIR = r"C:\Users\Usuario(a) Master\AppData\Local\Android\Sdk"
JDK_ZIP_URL = "https://api.adoptium.net/v3/binary/latest/17/ga/windows/x64/jdk/hotspot/normal/eclipse?project=jdk"

def ensure_jdk17():
    javac_exe = os.path.join(JDK_DIR, "bin", "javac.exe")
    if os.path.exists(javac_exe):
        print(f"[OK] JDK 17 já encontrado em: {JDK_DIR}")
        return JDK_DIR

    # Checa se está em subpasta extraída
    if os.path.exists(JDK_DIR):
        for sub in os.listdir(JDK_DIR):
            sub_javac = os.path.join(JDK_DIR, sub, "bin", "javac.exe")
            if os.path.exists(sub_javac):
                print(f"[OK] JDK 17 encontrado em subpasta: {os.path.join(JDK_DIR, sub)}")
                return os.path.join(JDK_DIR, sub)

    print(f"[*] Baixando OpenJDK 17 portátil da Adoptium...")
    os.makedirs(r"C:\Users\Usuario(a) Master\.jdks", exist_ok=True)
    temp_zip = r"C:\Users\Usuario(a) Master\.jdks\temurin17.zip"

    ssl_ctx = ssl._create_unverified_context()
    req = urllib.request.Request(JDK_ZIP_URL, headers={"User-Agent": "Mozilla/5.0"})
    t0 = time.time()
    with urllib.request.urlopen(req, context=ssl_ctx) as resp, open(temp_zip, "wb") as out:
        total = int(resp.headers.get("content-length", 0))
        downloaded = 0
        while True:
            chunk = resp.read(1024 * 1024)
            if not chunk:
                break
            out.write(chunk)
            downloaded += len(chunk)
            if total > 0:
                pct = (downloaded / total) * 100
                print(f"\rProgresso: {downloaded / (1024*1024):.1f}/{total / (1024*1024):.1f} MB ({pct:.1f}%)", end="", flush=True)

    print(f"\n[OK] Download concluído em {time.time() - t0:.1f}s. Extraindo...")
    with zipfile.ZipFile(temp_zip, "r") as zf:
        zf.extractall(JDK_DIR)
    
    try:
        os.remove(temp_zip)
    except Exception:
        pass

    # Acha o diretório do JDK
    for root, dirs, files in os.walk(JDK_DIR):
        if "javac.exe" in files and "bin" in root:
            home = os.path.dirname(root)
            print(f"[OK] JDK 17 instalado e configurado em: {home}")
            return home

    return JDK_DIR

def run_gradle_build(java_home):
    print("\n" + "="*60)
    print(" INICIANDO COMPILAÇÃO DO APK (DEBUG) VIA GRADLE")
    print("="*60)

    env = os.environ.copy()
    env["JAVA_HOME"] = java_home
    env["ANDROID_HOME"] = SDK_DIR
    env["PATH"] = os.path.join(java_home, "bin") + os.pathsep + env.get("PATH", "")

    android_dir = os.path.join(os.getcwd(), "android")
    gradlew_bat = os.path.join(android_dir, "gradlew.bat")

    cmd = [gradlew_bat, "assembleDebug", "--no-daemon"]
    print(f"Executando: {' '.join(cmd)}")
    print(f"JAVA_HOME={java_home}")
    print(f"ANDROID_HOME={SDK_DIR}")

    t0 = time.time()
    p = subprocess.Popen(
        cmd,
        cwd=android_dir,
        env=env,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        bufsize=1
    )

    for line in iter(p.stdout.readline, ""):
        print(line, end="")

    p.stdout.close()
    p.wait()

    elapsed = time.time() - t0
    print(f"\nProcesso finalizado em {elapsed:.1f}s com código de retorno: {p.returncode}")

    apk_path = os.path.join(android_dir, "app", "build", "outputs", "apk", "debug", "app-debug.apk")
    if os.path.exists(apk_path):
        size_mb = os.path.getsize(apk_path) / (1024 * 1024)
        print("\n" + "="*60)
        print(f" SUCESSO! APK GERADO COM SUCESSO:")
        print(f" Caminho: {apk_path}")
        print(f" Tamanho: {size_mb:.2f} MB")
        print("="*60)
        return True
    else:
        print("\n[ALERTA] Arquivo APK não encontrado no local padrão.")
        return False

if __name__ == "__main__":
    jdk = ensure_jdk17()
    success = run_gradle_build(jdk)
    sys.exit(0 if success else 1)
