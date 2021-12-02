package mrtjp.projectred.core.libmc

import codechicken.lib.vec.{BlockCoord, Vector3}
import codechicken.multipart.{TMultiPart, TileMultipart}
import net.minecraft.block.Block
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.{IBlockAccess, World}

object PRLib
{
    def dropTowardsPlayer(w:World, bc:BlockCoord, stack:ItemStack, p:EntityPlayer)
    {
        dropTowardsPlayer(w, bc.x, bc.y, bc.z, stack, p)
    }
    def dropTowardsPlayer(w:World, x:Int, y:Int, z:Int, stack:ItemStack, p:EntityPlayer)
    {
        if (!w.isRemote && w.getGameRules.getGameRuleBooleanValue("doTileDrops"))
        {
            val bpos = new Vector3(x, y, z)
            val d = new Vector3(p.posX, p.posY, p.posZ).subtract(bpos).normalize()
            val vel = d.copy.multiply(8.0)
            val pos = bpos.add(Vector3.center).add(d.copy.multiply(1.25))

            val item = new EntityItem(w, pos.x, pos.y, pos.z, stack)
            item.motionX = vel.x*0.02
            item.motionY = vel.y*0.02
            item.motionZ = vel.z*0.02
            item.delayBeforeCanPickup = 0
            w.spawnEntityInWorld(item)
        }
    }

    def getMultipartTile(w:IBlockAccess, bc:BlockCoord):TileMultipart = getMultipartTile(w, bc.x, bc.y, bc.z)
    def getMultipartTile(w:IBlockAccess, x:Int, y:Int, z:Int) = w.getTileEntity(x, y, z) match
    {
        case t:TileMultipart => t
        case _ => null
    }

    def getMultiPart(w:IBlockAccess, bc:BlockCoord, part:Int):TMultiPart = getMultiPart(w, bc.x, bc.y, bc.z, part)
    def getMultiPart(w:IBlockAccess, x:Int, y:Int, z:Int, part:Int) =
    {
        val t = getMultipartTile(w, x, y, z)
        if (t != null) t.partMap(part)
        else null
    }
}
