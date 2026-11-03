"""Draw the mod's logo on a 32x32 grid: Dragon Survival's Elder Dragon Bone in a pixel-art obsidian portal.

usage: python tools/make_logo.py <Dragon Survival jar>
Writes src/main/resources/logo.png (256 px, mod list) and release/curseforge_avatar.png (512 px); the grid is only
ever scaled by whole numbers, so every logo pixel stays square and sharp.
"""
import io
import math
import os
import random
import sys
import zipfile

from PIL import Image

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..")
BONE = "assets/dragonsurvival/textures/item/elder_dragon_bone.png"
GRID = 32
FRAME = 4  # obsidian frame width

SWIRL = [(45, 10, 104), (70, 16, 150), (103, 28, 196), (140, 54, 232), (181, 104, 250)]
OBSIDIAN = [(15, 10, 24), (22, 15, 36), (32, 22, 52), (48, 34, 77)]
RIM_GLOW = (214, 164, 255)
OUTLINE = (24, 6, 52)


def swirl(x, y, rng):
    """Two arms winding around the centre, lighter towards the middle, a little noise to break up the bands."""
    dx, dy = x - (GRID - 1) / 2, y - (GRID - 1) / 2
    radius = math.hypot(dx, dy)
    wave = math.sin(2 * math.atan2(dy, dx) + radius * 0.75)
    level = 1.7 + 1.5 * wave + rng.uniform(-0.45, 0.45)
    return SWIRL[max(0, min(len(SWIRL) - 1, round(level)))]


def obsidian(x, y, rng):
    """Dark speckled stone, lit along the top and left of the frame, its corners cut one pixel."""
    far = GRID - 1
    if (x in (0, far)) and (y in (0, far)):
        return (0, 0, 0, 0)
    if y == 0 or x == 0:
        return (*OBSIDIAN[3], 255)
    if y == far or x == far:
        return (*OBSIDIAN[0], 255)
    return (*OBSIDIAN[rng.choice((0, 1, 1, 1, 2, 2))], 255)


def main():
    bone = Image.open(io.BytesIO(zipfile.ZipFile(sys.argv[1]).read(BONE))).convert("RGBA")
    rng = random.Random(1024)
    logo = Image.new("RGBA", (GRID, GRID), (0, 0, 0, 0))

    for y in range(GRID):
        for x in range(GRID):
            inside = FRAME <= x < GRID - FRAME and FRAME <= y < GRID - FRAME
            logo.putpixel((x, y), (*swirl(x, y, rng), 255) if inside else obsidian(x, y, rng))

    # the glowing inner edge of the frame, where the portal meets the stone
    low, high = FRAME, GRID - FRAME - 1
    for i in range(low, high + 1):
        colour = (*RIM_GLOW, 255) if i % 3 else (*SWIRL[4], 255)
        for pos in ((i, low), (low, i), (i, high), (high, i)):
            logo.putpixel(pos, colour)

    # the bone in the middle, pixel for pixel, with a one pixel outline so it reads against the swirl
    left, top = (GRID - bone.width) // 2, (GRID - bone.height) // 2
    solid = {(x, y) for x in range(bone.width) for y in range(bone.height) if bone.getpixel((x, y))[3] > 0}
    for x, y in solid:
        for ox, oy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            if (x + ox, y + oy) not in solid:
                logo.putpixel((left + x + ox, top + y + oy), (*OUTLINE, 255))
    for x, y in solid:
        logo.putpixel((left + x, top + y), bone.getpixel((x, y)))

    os.makedirs(os.path.join(ROOT, "release"), exist_ok=True)
    logo.resize((256, 256), Image.NEAREST).save(os.path.join(ROOT, "src", "main", "resources", "logo.png"))
    logo.resize((512, 512), Image.NEAREST).save(os.path.join(ROOT, "release", "curseforge_avatar.png"))


main()
