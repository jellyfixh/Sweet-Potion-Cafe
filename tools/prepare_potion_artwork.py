from PIL import Image, ImageDraw
from pathlib import Path

OUT = Path('asset/potions')
OUT.mkdir(parents=True, exist_ok=True)

# name, liquid, glow, style, motifs, ingredient accent
POTIONS = [
 ('elixir_of_the_deep','#1bc9bd','#62fff0','round','bubbles spores','cyan'),
 ('tidal_vitality','#236bc5','#75bfff','tall','wave roots','green'),
 ('prism_protection','#d334a6','#ff7cdb','faceted','shield scales','gold'),
 ('lunar_aurora','#a783d5','#e9c7ff','star','mist rainbow','pink'),
 ('starlight_slumber','#afcfe0','#d9f4ff','crescent','stars frost','white'),
 ('permafrost_tonic','#45a9dc','#9cecff','square','ice shards spores','cyan'),
 ('solar_renewal','#eaa62d','#ffdc65','round','sun roots','green'),
 ('flame_of_dawn','#e95c27','#ff9b43','spiked','flame scales','gold'),
 ('ancient_blaze','#922b3d','#ff6248','bark','cracks roots','orange'),
 ('moonlight_spore','#8047bb','#c183f6','jar','spores moon','cyan'),
 ('sirens_frozen_heart','#62bde3','#aff3ff','crystal','heart snow','white'),
 ('celestial_ocean_breeze','#2bc5b6','#8bfff0','star','stars rainbow','pink'),
 ('leviathans_blessing','#25aaba','#75faff','spiked','waves scales roots','gold'),
 ('world_tree_sap','#3ea35c','#91ed8c','bark','leaves rainbow spores','pink'),
 ('winter_dragon_breath','#4289c7','#ff9e64','spiked','split flame snow','orange'),
 ('solar_phoenix_elixir','#c87520','#ffdf67','faceted','sun feathers','red'),
 ('astral_dreamscape','#6745aa','#c393ff','orb','stars spores','cyan'),
 ('frostfire_aegis','#78cbd1','#c3ffff','crystal','shield ice flame','orange'),
 ('moonlit_tide_tonic','#286aa5','#a3d3ff','crescent','moon waves spores','silver'),
 ('ancient_sun_remedy','#bd7727','#ffcf66','bark','sun roots','green'),
 ('dragon_roots_draught','#702d3c','#ff8874','jar','scales roots','green'),
 ('glacial_starlight_brew','#9bc8dc','#ffe7a0','round','snow star','gold'),
 ('rainbow_miracle_elixir','#8bbec0','#fff08a','crystal','rainbow stars','pink'),
 ('abyssal_dragon_tears','#292e70','#958cff','spiked','dragon tear scales','red'),
]

OUTLINE=(48,36,61,255); GLASS=(177,239,224,255); SHADE=(75,128,132,255)
ACCENTS={'cyan':(114,255,239,255),'green':(125,190,87,255),'gold':(255,207,93,255),
 'pink':(255,143,215,255),'white':(231,252,255,255),'orange':(255,119,57,255),
 'red':(245,83,93,255),'silver':(213,226,239,255)}

def pix(d, pts, c):
    for x,y in pts: d.point((x,y), fill=c)

def make(item, i):
    name, liquid, glow, shape, motifs, accent=item
    im=Image.new('RGBA',(32,32),(0,0,0,0)); d=ImageDraw.Draw(im)
    lc=Image.new('RGBA',(32,32),(0,0,0,0)); ld=ImageDraw.Draw(lc)
    L=tuple(bytes.fromhex(liquid[1:]))+(255,); G=tuple(bytes.fromhex(glow[1:]))+(255,); A=ACCENTS[accent]
    # Small pixel clusters of emitted light echo the luminous herb sprites.
    for x,y,a in [(16,8,42),(7,14,26),(25,14,26),(6,22,22),(26,22,22),(12,28,30),(20,28,30)]:
        d.point((x,y), fill=(*G[:3],a))
    for x,y in [(8,13),(24,17),(10,24),(22,25),(16,6)]: d.point((x,y),fill=(*G[:3],52))
    # Vessel silhouette, with varied shoulders and bases.
    d.rectangle((13,4,18,5), fill=OUTLINE); d.rectangle((14,4,17,4), fill=(157,111,68,255))
    d.rectangle((14,6,17,8), fill=OUTLINE); d.rectangle((15,6,16,7), fill=GLASS)
    if shape in ('square','jar','bark'):
        d.rectangle((10,9,21,24), fill=OUTLINE); d.rectangle((11,10,20,23), fill=GLASS)
        d.rectangle((9,11,22,20), fill=OUTLINE); d.rectangle((10,12,21,20), fill=GLASS)
        left,right=11,20
    elif shape in ('faceted','crystal'):
        d.polygon([(14,9),(18,9),(22,12),(23,20),(19,25),(13,25),(9,20),(10,12)], fill=OUTLINE)
        d.polygon([(14,10),(18,10),(21,13),(21,20),(18,24),(14,24),(11,20),(11,13)], fill=GLASS)
        left,right=12,20
    elif shape=='star':
        d.polygon([(14,9),(18,9),(20,13),(23,14),(21,18),(22,22),(17,21),(13,23),(12,19),(9,16),(12,13)], fill=OUTLINE)
        d.polygon([(14,10),(18,10),(19,14),(21,15),(20,18),(20,21),(17,20),(14,22),(13,18),(11,16),(13,14)], fill=GLASS)
        left,right=13,20
    elif shape=='crescent':
        d.polygon([(13,9),(19,9),(22,12),(23,20),(20,24),(14,25),(10,22),(9,16),(10,12)], fill=OUTLINE)
        d.polygon([(13,10),(18,10),(21,13),(21,20),(19,23),(14,24),(11,21),(10,16),(11,13)], fill=GLASS)
        left,right=11,20
    elif shape=='orb':
        d.ellipse((9,9,22,25), fill=OUTLINE); d.ellipse((10,10,21,24), fill=GLASS)
        left,right=11,20
    elif shape=='spiked':
        d.polygon([(13,9),(19,9),(21,11),(24,10),(23,14),(25,17),(22,20),(21,24),(17,25),(12,24),(10,21),(8,18),(10,14),(9,11)], fill=OUTLINE)
        d.polygon([(14,10),(18,10),(20,12),(22,12),(21,15),(23,17),(21,20),(20,23),(17,24),(13,23),(11,20),(10,18),(12,15),(11,12)], fill=GLASS)
        left,right=12,21
    else:
        d.ellipse((9,9,22,25), fill=OUTLINE); d.ellipse((10,10,21,24), fill=GLASS)
        left,right=11,20
    # Liquid fill at the lower half, held inside the glass outline.
    ld.rectangle((left+1,18,right-1,22), fill=L); ld.rectangle((left+2,17,right-2,17), fill=G)
    # top glow through the glass and clean reflective pixels
    d.rectangle((11,11,12,15), fill=(235,255,244,255)); d.point((13,16), fill=GLASS)
    d.rectangle((19,13,20,15), fill=SHADE)
    # Light pool and floating light specks remain compact and pixelated.
    d.rectangle((11,26,20,27), fill=(47,32,58,210)); d.rectangle((13,26,18,26), fill=(*G[:3],175))
    # Bottle-specific material decorations.
    if shape=='bark':
        pix(d,[(10,13),(11,14),(10,15),(21,18),(20,19),(21,20),(12,22)],(139,100,58,255))
        pix(d,[(11,17),(21,22)],ACCENTS['green'])
    if shape=='spiked':
        pix(d,[(9,12),(8,11),(23,12),(24,11)],(145,91,61,255))
    if shape=='crystal': pix(d,[(11,14),(12,13),(19,21),(20,20)],(220,251,255,255))
    # Recipe motifs are deliberately tiny to survive nearest-neighbor scaling.
    if 'spores' in motifs:
        pix(d,[(16,12),(17,13),(14,15),(19,15),(16,20)],G)
        pix(d,[(15,11),(18,14),(13,14)],(235,255,210,255))
    if 'bubbles' in motifs: pix(d,[(14,13),(18,15),(16,21)],(220,255,250,255))
    if 'roots' in motifs: pix(d,[(12,15),(13,16),(14,17),(19,18),(18,19),(17,20)],ACCENTS['green'])
    if 'scales' in motifs: pix(d,[(12,14),(13,13),(19,14),(20,15),(14,20),(19,21)],A)
    if 'ice' in motifs or 'snow' in motifs:
        pix(d,[(16,12),(16,16),(14,14),(18,14),(15,13),(17,15)],(229,255,255,255))
    if 'stars' in motifs: pix(d,[(16,12),(16,14),(15,13),(17,13),(18,19),(18,20),(17,19)],(255,242,170,255))
    if 'moon' in motifs: pix(d,[(17,12),(18,13),(17,14),(16,14)],(238,229,255,255))
    if 'sun' in motifs: pix(d,[(16,12),(15,13),(16,13),(17,13),(16,14),(16,11)],(255,225,106,255))
    if 'heart' in motifs: pix(d,[(15,13),(17,13),(14,14),(18,14),(15,15),(17,15),(16,16)],(255,119,154,255))
    if 'shield' in motifs: pix(d,[(15,12),(17,12),(15,13),(17,13),(16,14),(16,15)],A)
    if 'flame' in motifs: pix(d,[(16,12),(15,14),(16,15),(17,14),(16,18)],(255,138,65,255))
    if 'split' in motifs:
        ld.rectangle((12,18,15,22),fill=(72,157,225,255)); ld.rectangle((16,18,20,22),fill=(233,101,44,255))
    if 'rainbow' in motifs:
        pix(d,[(14,18),(15,18),(16,18)],(244,103,110,255)); pix(d,[(14,19),(15,19),(16,19)],(255,206,91,255)); pix(d,[(17,19),(18,19)],(94,222,153,255)); pix(d,[(17,18),(18,18)],(122,162,255,255))
    if 'mist' in motifs: pix(d,[(12,14),(13,13),(19,14),(20,13),(14,16),(18,16)],A)
    if 'wave' in motifs: pix(d,[(12,20),(14,19),(16,20),(18,19),(20,20)],(127,220,249,255))
    if 'cracks' in motifs: pix(d,[(12,17),(13,18),(19,19),(18,20)],(255,104,55,255))
    if 'leaves' in motifs: pix(d,[(14,13),(15,12),(17,13),(18,14),(14,15)],ACCENTS['green'])
    if 'feathers' in motifs: pix(d,[(14,14),(15,13),(15,15),(18,14),(17,13),(17,15)],(255,195,66,255))
    if 'dragon' in motifs: pix(d,[(15,12),(16,11),(17,12),(14,13),(18,13)],A)
    if 'tear' in motifs: pix(d,[(16,12),(15,13),(16,14),(17,13)],(170,178,255,255))
    im.alpha_composite(lc)
    return im

for i,p in enumerate(POTIONS):
    make(p,i).save(OUT/(p[0]+'.png'), optimize=True)
print(f'Wrote {len(POTIONS)} transparent 32x32 potion sprites to {OUT}')
