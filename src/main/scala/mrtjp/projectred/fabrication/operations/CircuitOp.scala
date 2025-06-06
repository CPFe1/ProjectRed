/*
 * Copyright (c) 2015.
 * Created by MrTJP.
 * All rights reserved.
 */
package mrtjp.projectred.fabrication.operations

import codechicken.lib.data.{MCDataInput, MCDataOutput}
import codechicken.lib.gui.GuiDraw
import cpw.mods.fml.relauncher.{Side, SideOnly}
import mrtjp.core.vec.{Point, Size, Vec2}
import mrtjp.projectred.fabrication.{IntegratedCircuit, RenderCircuit}


trait CircuitOp {
  var id = -1

  def canPlace(circuit: IntegratedCircuit, position: Vec2): Boolean = false

  def checkOp(circuit: IntegratedCircuit, start: Point, end: Point): Boolean =
    canPlace(circuit, start.vectorize) && circuit.getPart(start) == null

  def getRotation(): Int

  def getConfiguration(): Int

  def writeOp(
               circuit: IntegratedCircuit,
               start: Point,
               end: Point,
               rotation: Int,
               configuration: Int,
               out: MCDataOutput
             )

  def readOp(circuit: IntegratedCircuit, in: MCDataInput)

  @SideOnly(Side.CLIENT)
  def getOpName: String

  /**
   * Render the selected Operation, when on the mouse cursor
   * @param position Position in Grid coordinates
   * @param scale Prefboard scaling
   */
  @SideOnly(Side.CLIENT)
  def renderHover(circuit: IntegratedCircuit, position: Vec2, scale: Double, prefboardOffset: Vec2): Unit

  /**
   * Render the selected Operation when dragging
   * @param start Start in Grid coordinates
   * @param end End in Grid coordinates
   * @param positionsWithParts Positions in Rect(start, end) with existing parts
   * @param scale Prefboard scaling
   * @param prefboardOffset Prefboard offset
   */
  @SideOnly(Side.CLIENT)
  def renderDrag(circuit: IntegratedCircuit, start: Vec2, end: Vec2, positionsWithParts: Seq[Vec2], scale: Double, prefboardOffset: Vec2): Unit

  /**
   * Render the part, that will be placed by this operation
   */
  @SideOnly(Side.CLIENT)
  def renderImage(x: Double, y: Double, width: Double, height: Double)

  /**
   * Same as renderImage, however it ignores configuration and rotation (e.g. toolbar)
   */
  @SideOnly(Side.CLIENT)
  def renderImageStatic(x: Double, y: Double, width: Double, height: Double): Unit =
    renderImage(x, y, width, height)
}

object CircuitOp {
  def getOperation(id: Int) = CircuitOpDefs(id).getOp

  /**
   * Draw one Tile
   * @param position In Grid Coordinates
   */
  def renderHolo(position: Vec2, scale: Double, colour: Int): Unit = {
    val start = position * RenderCircuit.BASE_SCALE * scale
    val end = start + Vec2(RenderCircuit.BASE_SCALE * scale, RenderCircuit.BASE_SCALE * scale)
    GuiDraw.drawRect(start.dx.toInt, start.dy.toInt, (end - start).dx.toInt, (end - start).dy.toInt, colour)
  }

  /**
   * Draw Multiple tiles in a rect excluding end
   * @param start In Grid Coordinates
   * @param end In Grid coordinates
   */
  def renderHolo(start: Vec2, end: Vec2, scale: Double, colour: Int): Unit = {
    val s = start * RenderCircuit.BASE_SCALE * scale
    val e = end * RenderCircuit.BASE_SCALE * scale
    GuiDraw.drawRect(s.dx.toInt, s.dy.toInt, (e - s).dx.toInt, (e - s).dy.toInt, colour)
  }

  def partsBetweenPoints(start: Vec2, end: Vec2, circuit: IntegratedCircuit): Seq[Vec2] = {
    var partPositions: Seq[Vec2] = Seq.empty
    for(x <- math.min(start.dx.toInt, end.dx.toInt) to math.max(start.dx.toInt, end.dx.toInt)) {
      for(y <- math.min(start.dy.toInt, end.dy.toInt) to math.max(start.dy.toInt, end.dy.toInt)) {
        if(circuit.getPart(x, y) != null) {
          partPositions = partPositions :+ Vec2(x, y)
        }
      }
    }
    partPositions
  }

  def isOnBorder(circuit: IntegratedCircuit, position: Vec2) =
    position.dx == 0 || position.dy == 0 || position.dx == circuit.size.width - 1 || position.dy == circuit.size.height - 1

  def isOnEdge(circuit: IntegratedCircuit, position: Vec2) =
    position == Vec2(0, 0) ||
      position == Vec2(0, circuit.size.height - 1) ||
      position == Vec2(circuit.size.width - 1, 0) ||
      position == Vec2(circuit.size.width - 1, circuit.size.height - 1)
}
