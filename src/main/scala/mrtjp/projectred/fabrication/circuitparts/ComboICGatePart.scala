package mrtjp.projectred.fabrication.circuitparts

class ComboICGatePart extends RedstoneGateICPart {
  override def getLogic[T] = ComboICGateLogic.instances(subID).asInstanceOf[T]

  def getLogicCombo = getLogic[ComboICGateLogic]

  override def getPartType = CircuitPartDefs.SimpleGate
}
