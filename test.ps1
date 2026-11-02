$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    & (Join-Path $PSScriptRoot 'build.ps1')
    foreach ($potionTest in @('IngredientStockTest', 'GameLogicTest', 'CampaignTest', 'AssetLoadingTest', 'CustomerSpriteTest', 'VisualSmokeTest')) {
        & java '-Djava.awt.headless=true' '-Dfile.encoding=UTF-8' -cp bin "com.potioncraft.test.$potionTest"
        if ($LASTEXITCODE -ne 0) { throw "Test failed: $potionTest" }
    }
    # Launch like an IDE from a different working folder containing stale artwork.
    $potionForeignRoot = Join-Path $PSScriptRoot ('build/asset-path-test-' + [Guid]::NewGuid().ToString('N'))
    $potionForeignSprites = Join-Path $potionForeignRoot 'asset/customers/elder_herbalist'
    $potionForeignBackground = Join-Path $potionForeignRoot 'asset/background'
    New-Item -ItemType Directory -Path $potionForeignSprites, $potionForeignBackground -Force | Out-Null
    Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'asset/customers/local_farmer/normal.png') -Destination (Join-Path $potionForeignSprites 'normal.png')
    Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'asset/background/herb_box_cutout.png') -Destination (Join-Path $potionForeignBackground 'background_no_counter.png')
    Push-Location $potionForeignRoot
    try {
        foreach ($potionClasspath in @((Join-Path $PSScriptRoot 'bin'), (Join-Path $PSScriptRoot 'dist/PotionAtelier-v2.jar'))) {
            & java '-Djava.awt.headless=true' -cp $potionClasspath com.potioncraft.test.AssetLoadingTest $PSScriptRoot
            if ($LASTEXITCODE -ne 0) { throw 'IDE or packaged game loaded stale artwork from the working folder.' }
        }
        Write-Host 'IDE and JAR launches both load current artwork despite stale files in the working folder.'
    } finally { Pop-Location }
    Write-Host 'All tests passed. Screenshots: build/screenshots'
} finally {
    Pop-Location
}
