package io.github.kamilkime.kcaptcha.bossbar.versioned;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.github.kamilkime.kcaptcha.bossbar.KBoss;
import io.github.kamilkime.kcaptcha.enums.BossType;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;

public class v1_8_R3 extends KBoss {
	
	public v1_8_R3(String name, Location location, float startPercent, Object color, Object style, BossType bossType) {
		super(name, location, startPercent, color, style, bossType);
	}

	@Override
	public void sendMetaPacket(Player p) {
		DataWatcher d = new DataWatcher((Entity) this.boss);
		d.a(0, (byte) 0x20);
		d.a(6, this.hp);
		sendPacket(p, new PacketPlayOutEntityMetadata(((Entity) this.boss).getId(), d, true));
	}
	
	@Override
	public void sendSpawnPacket(Player p) {
		this.boss = (this.bossType.equals(BossType.DRAGON) ? new EntityEnderDragon(((CraftWorld) this.location.getWorld()).getHandle())
				: new EntityWither(((CraftWorld) this.location.getWorld()).getHandle()));
		((EntityLiving) this.boss).setLocation(this.location.getX(), this.location.getY(), this.location.getZ(), this.location.getYaw(), this.location.getPitch());
		((EntityLiving) this.boss).setHealth(this.hp);
		((EntityLiving) this.boss).setInvisible(true);
		((EntityLiving) this.boss).setCustomName(this.name);
		sendPacket(p, new PacketPlayOutSpawnEntityLiving((EntityLiving) this.boss));
	}

	@Override
	public void sendDestroyPacket(Player p) {
		sendPacket(p, new PacketPlayOutEntityDestroy(((Entity) this.boss).getId()));
	}

	@Override
	public void sendTeleportPacket(Player p, Location to) {
		sendPacket(p, new PacketPlayOutEntityTeleport(((Entity) this.boss).getId(), (int) (to.getX() * 32.0D), (int) (to.getY() * 32.0D), (int) (to.getZ() * 32.0D),
				((byte)(int)(to.getYaw() * 256.0F / 360.0F)), ((byte)(int)(to.getPitch() * 256.0F / 360.0F)), false));
	}
	
	@Override
	public void sendPacket(Player p, Object packet) {
		((CraftPlayer) p).getHandle().playerConnection.sendPacket((Packet<?>) packet);
	}
}