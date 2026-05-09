Add-Type -AssemblyName System.Drawing

$sourcePath = "C:\Users\user\.gemini\antigravity\brain\6d19ba0b-ed0a-413a-80f3-89e12ad4a132\media__1774879459589.png"
$basePath = "c:\Users\user\Desktop\sensixpert\app\src\main\res"

$sourceImg = [System.Drawing.Image]::FromFile($sourcePath)
Write-Host "Source image: $($sourceImg.Width) x $($sourceImg.Height)"

$densities = @{
    "mipmap-mdpi"    = 48
    "mipmap-hdpi"    = 72
    "mipmap-xhdpi"   = 96
    "mipmap-xxhdpi"  = 144
    "mipmap-xxxhdpi" = 192
}

foreach ($density in $densities.GetEnumerator()) {
    $dir = Join-Path $basePath $density.Key
    $size = $density.Value
    
    # Remove ALL old icon files (png, webp)
    Get-ChildItem -Path $dir -Filter "ic_launcher*" -ErrorAction SilentlyContinue | Remove-Item -Force
    
    # Create resized bitmap
    $bmp = New-Object System.Drawing.Bitmap($size, $size)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
    
    # Fill white background first
    $g.Clear([System.Drawing.Color]::White)
    
    # Draw the source image scaled to fit
    $g.DrawImage($sourceImg, 0, 0, $size, $size)
    
    $g.Dispose()
    
    # Save as PNG - ic_launcher
    $launcherPath = Join-Path $dir "ic_launcher.png"
    $bmp.Save($launcherPath, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "Generated: $launcherPath ($size x $size)"
    
    # Save as PNG - ic_launcher_round (same image)
    $roundPath = Join-Path $dir "ic_launcher_round.png"
    $bmp.Save($roundPath, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "Generated: $roundPath ($size x $size)"
    
    $bmp.Dispose()
}

$sourceImg.Dispose()

# Remove adaptive icon XML files so Android uses the PNG directly
$anydpiDir = Join-Path $basePath "mipmap-anydpi-v26"
if (Test-Path $anydpiDir) {
    Remove-Item $anydpiDir -Recurse -Force
    Write-Host "`nRemoved mipmap-anydpi-v26 (adaptive icon config) so exact PNG is used"
}

Write-Host "`nDone! Exact logo image applied to all densities."
