package buildcraft.oiltweak;

import buildcraft.oiltweak.api.OilTweakAPI;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * @author Vexatos
 */
public class OilTweakEventHandler {

	private enum InOil {
		NONE, HALF, FULL;

		public boolean halfOfFull() {
			return this != NONE;
		}
	}

	protected InOil getInOil(Entity entity) {
		//Taken from Entity class
		int minX = MathHelper.floor_double(entity.boundingBox.minX + 0.001D);
		int minY = MathHelper.floor_double(entity.boundingBox.minY + 0.001D);
		int minZ = MathHelper.floor_double(entity.boundingBox.minZ + 0.001D);
		int maxX = MathHelper.floor_double(entity.boundingBox.maxX - 0.001D);
		int maxY = MathHelper.floor_double(entity.boundingBox.maxY - 0.001D);
		int maxZ = MathHelper.floor_double(entity.boundingBox.maxZ - 0.001D);

		if(entity.worldObj.checkChunksExist(minX, minY, minZ, maxX, maxY, maxZ)) {
			for(int x = minX; x <= maxX; ++x) {
				for(int y = minY; y <= maxY; ++y) {
					for(int z = minZ; z <= maxZ; ++z) {
						if(isOil(entity.worldObj.getBlock(x, y, z))) {
							return (int) (y + entity.getEyeHeight()) != y && isOil(entity.worldObj.getBlock(x, (int) (y + entity.getEyeHeight()), z)) ? InOil.FULL : InOil.HALF;
						}
					}
				}
			}
		}
		return InOil.NONE;
	}

	private boolean isOil(Block block) {
		if(block == null) {
			return false;
		}
		Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
		return fluid != null
			&& FluidRegistry.isFluidRegistered(fluid)
			&& fluid.getName() != null
			&& fluid.getName().equalsIgnoreCase("oil");
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerUpdate(TickEvent.PlayerTickEvent e) {
		if(!BuildCraftOilTweak.config.isOilDense()) {
			return;
		}
		EntityPlayer player = e.player;
		if(!getInOil(player).halfOfFull()) {
			return;
		}
		player.motionY = Math.min(0.0D, player.motionY);
		if(player.motionY < -0.05D) {
			player.motionY *= 0.05D;
		}

		player.motionX = Math.max(-0.05D, Math.min(0.05D, player.motionX * 0.05D));
		player.motionY -= 0.05D;
		player.motionZ = Math.max(-0.05D, Math.min(0.05D, player.motionZ * 0.05D));
		player.capabilities.isFlying = player.capabilities.isFlying && player.capabilities.isCreativeMode;
		player.stepHeight = 0.0F;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerClientUpdate(TickEvent.ClientTickEvent e) {
		if(!BuildCraftOilTweak.config.isOilDense()) {
			return;
		}
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		if(player == null || !getInOil(player).halfOfFull()) {
			return;
		}
		player.motionY = Math.min(0.0D, player.motionY);
		if(player.motionY < -0.05D) {
			player.motionY *= 0.05D;
		}

		player.motionX = Math.max(-0.05D, Math.min(0.05D, player.motionX * 0.05D));
		player.motionY -= 0.05D;
		player.motionZ = Math.max(-0.05D, Math.min(0.05D, player.motionZ * 0.05D));
		player.capabilities.isFlying = player.capabilities.isFlying && player.capabilities.isCreativeMode;
		player.stepHeight = 0.0F;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent e) {
		if(!BuildCraftOilTweak.config.isOilDense()) {
			return;
		}
		EntityLivingBase entity = e.entityLiving;
		if(!getInOil(entity).halfOfFull()) {
			return;
		}
		entity.motionY = Math.min(0.0D, entity.motionY);
		if(entity.motionY < -0.05D) {
			entity.motionY *= 0.05D;
		}

		entity.motionX = Math.max(-0.05D, Math.min(0.05D, entity.motionX * 0.05D));
		entity.motionY -= 0.05D;
		entity.motionZ = Math.max(-0.05D, Math.min(0.05D, entity.motionZ * 0.05D));
		entity.stepHeight = 0.0F;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onBreakSpeed(PlayerEvent.BreakSpeed e) {
		if(!BuildCraftOilTweak.config.isOilDense()) {
			return;
		}
		EntityPlayer player = e.entityPlayer;
		if(getInOil(player).halfOfFull()) {
			e.newSpeed = e.originalSpeed <= e.newSpeed ? e.originalSpeed / 3f : e.newSpeed / 3f;
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onTeleportAttempt(EnderTeleportEvent e) {
		if(!BuildCraftOilTweak.config.isOilDense()) {
			return;
		}
		EntityLivingBase player = e.entityLiving;
		if(getInOil(player).halfOfFull()) {
			e.setCanceled(true);
			e.setResult(Event.Result.DENY);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onRightClick(PlayerInteractEvent e) {
		if(!BuildCraftOilTweak.config.isOilDense()) {
			return;
		}
		EntityPlayer player = e.entityPlayer;
		if(!player.capabilities.isCreativeMode && OilTweakAPI.INSTANCE.getItemBlacklistRegistry().isBlacklisted(player, player.inventory.getCurrentItem())) {
			InOil inOil = getInOil(player);
			player.addChatComponentMessage(new ChatComponentTranslation(inOil == InOil.FULL ?
				"oiltweak.chat.tooDense.use" : "oiltweak.chat.tooDense.use.half"));
			e.setCanceled(true);
		}
	}
}
