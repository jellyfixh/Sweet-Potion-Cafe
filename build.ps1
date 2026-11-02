$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
        throw 'Java JDK 17 or newer is required. Add its bin directory to PATH.'
    }
    $potionJarCommand = Get-Command jar -ErrorAction SilentlyContinue
    if ($potionJarCommand) {
        $potionJarPath = $potionJarCommand.Source
    } else {
        # Oracle's Windows PATH shim can expose java/javac without exposing jar.
        $potionJavaInfo = New-Object System.Diagnostics.ProcessStartInfo
        $potionJavaInfo.FileName = (Get-Command java).Source
        $potionJavaInfo.Arguments = '-XshowSettings:properties -version'
        $potionJavaInfo.UseShellExecute = $false
        $potionJavaInfo.CreateNoWindow = $true
        $potionJavaInfo.RedirectStandardError = $true
        $potionJavaProcess = [System.Diagnostics.Process]::Start($potionJavaInfo)
        $potionSettings = $potionJavaProcess.StandardError.ReadToEnd()
        $potionJavaProcess.WaitForExit()
        if ($potionSettings -match '(?m)^\s*java.home = (.+)$') {
            $potionJdkRoot = $Matches[1].Trim()
            $potionJarPath = Join-Path $potionJdkRoot 'bin/jar.exe'
        }
        if (-not $potionJarPath -or -not (Test-Path -LiteralPath $potionJarPath)) {
            throw 'Cannot find jar. Install JDK 17+ and add its bin directory to PATH.'
        }
    }
    New-Item -ItemType Directory -Path bin, dist, build -Force | Out-Null
    # Remove compiled classes from earlier versions before producing the IDE/JAR output.
    $potionClassRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot 'bin/com/potioncraft'))
    $potionBinRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot 'bin'))
    if (-not $potionClassRoot.StartsWith($potionBinRoot + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase)) {
        throw 'Compiled class directory must stay inside bin.'
    }
    if (Test-Path -LiteralPath $potionClassRoot) {
        if ((Resolve-Path -LiteralPath $potionClassRoot).Path -ne $potionClassRoot) { throw 'Unexpected compiled class location.' }
        $potionClassItems = @(Get-Item -LiteralPath $potionClassRoot) + @(Get-ChildItem -LiteralPath $potionClassRoot -Force -Recurse)
        if (@($potionClassItems | Where-Object { $_.Attributes -band [IO.FileAttributes]::ReparsePoint }).Count -ne 0) {
            throw 'Compiled class directory must not contain linked paths.'
        }
        Remove-Item -LiteralPath $potionClassRoot -Recurse -Force
    }
    $potionSources = Get-ChildItem -LiteralPath src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
    & javac -encoding UTF-8 --release 17 -d bin $potionSources
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed.' }
    & java '-Djava.awt.headless=true' '-Dfile.encoding=UTF-8' -cp bin com.potioncraft.tools.ExportCustomerSprites
    if ($LASTEXITCODE -ne 0) { throw 'Customer sprite export failed.' }
    & java '-Djava.awt.headless=true' '-Dfile.encoding=UTF-8' -cp bin com.potioncraft.tools.PrepareHerbArtwork
    if ($LASTEXITCODE -ne 0) { throw 'Ingredient sprite export failed.' }
    & java '-Djava.awt.headless=true' '-Dfile.encoding=UTF-8' -cp bin com.potioncraft.tools.PreparePotionArtwork
    if ($LASTEXITCODE -ne 0) { throw 'Potion sprite export failed.' }
    & $potionJarPath --create --file dist/PotionAtelier-v2.jar --main-class com.potioncraft.Main -C bin com -C . asset
    if ($LASTEXITCODE -ne 0) { throw 'JAR packaging failed.' }
    Write-Host 'Built dist/PotionAtelier-v2.jar (Java 17+; no external dependencies).'
} finally {
    Pop-Location
}
