package net.ip.zip.item.weapons;

public record GunStats(
        float damage,
        float bulletSpeed,
        int fireRate,
        float spreadHip,
        float spreadAds,
        int magazineSize,
        int reloadTime,
        boolean hasScope,
        float scopeZoom,
        float recoilPitch,
        float recoilYaw,
        int pierce,
        boolean fullAuto
) {}