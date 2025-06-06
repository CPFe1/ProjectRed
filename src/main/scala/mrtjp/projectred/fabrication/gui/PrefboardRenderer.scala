/*
 * Copyright (c) 2015.
 * Created by MrTJP.
 * All rights reserved.
 */
package mrtjp.projectred.fabrication.gui

import codechicken.lib.math.MathHelper
import codechicken.lib.render.{CCModel, CCRenderState}
import codechicken.lib.render.uv.UVScale
import codechicken.lib.vec.{Rotation, Scale, TransformationList, Translation}
import mrtjp.core.vec.{Size, Vec2}
import mrtjp.projectred.fabrication.ICComponentStore.faceModels
import mrtjp.projectred.fabrication.{IntegratedCircuit, RenderCircuit}
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation


object PrefboardRenderer {

  def renderOrtho(circuit: IntegratedCircuit, scale: Double, gridTranslation: Vec2) {
    val offset = gridTranslation

    val state = CCRenderState.instance
    state.resetInstance()
    state.pullLightmapInstance()
    state.setDynamicInstance()

    renderBoard(circuit, scale, offset)
    renderEdges(circuit, scale, offset)
    renderCorners(circuit, scale, offset)
  }

  private def renderBoard(circuit: IntegratedCircuit, scale: Double, offset: Vec2): Unit = {
    val boardModel = faceModels.map(_.copy().apply(new UVScale(
      circuit.size.width,
      circuit.size.height
    )))
    val t = getTransform(circuit.size, scale, offset)
    draw("prefboard", t, boardModel)
  }

  private def renderEdges(circuit: IntegratedCircuit, scale: Double, offset: Vec2): Unit = {
    for((width, height, x, y) <- Seq(
      (circuit.size.width - 1, 1, 0, 0),
      (1, circuit.size.height - 1, -circuit.size.width + 1, 0),
      (circuit.size.width - 1, 1, -1, -circuit.size.height + 1),
      (1, circuit.size.height - 1, 0, -1)
    )) {
      val model = faceModels.map(_.copy().apply(new UVScale(
        width, height
      )))
      val transform = getTransform(Size(width, height), scale, offset + Vec2(x, y))
      draw("prefboard_edge", transform, model)
    }
  }

  private def renderCorners(circuit: IntegratedCircuit, scale: Double, offset: Vec2): Unit = {
    val model = faceModels.map(_.copy().apply(new UVScale(
      1, 1
    )))
    for((x, y) <- Seq(
      (0, 0),
      (-circuit.size.width + 1, 0),
      (0, -circuit.size.height + 1),
      (-circuit.size.width + 1, -circuit.size.height + 1)
    )) {
      val transform = getTransform(Size(1, 1), scale, offset + Vec2(x, y))
      draw("prefboard_corner", transform, model)
    }
  }

  private def draw(texName: String, transform: TransformationList, model: Seq[CCModel]): Unit = {
    val r2 = new ResourceLocation(
      "projectred",
      "textures/blocks/fabrication/" + texName + ".png"
    )
    Minecraft.getMinecraft.getTextureManager.bindTexture(r2)

    CCRenderState.instance.startDrawingInstance()
    model(1).render(transform)
    CCRenderState.instance.drawInstance()
  }

  private def getTransform(circuitSize: Size, scale: Double, offset: Vec2): TransformationList = {
    new TransformationList(
      new Scale(
        circuitSize.width * RenderCircuit.BASE_SCALE * scale,
        1,
        -(circuitSize.height * RenderCircuit.BASE_SCALE * scale)
      ),
      new Translation(-offset.dx * RenderCircuit.BASE_SCALE * scale, 0, offset.dy * RenderCircuit.BASE_SCALE * scale),
      new Rotation(0.5 * MathHelper.pi, 1, 0, 0)
    )
  }
}
