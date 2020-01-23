package rpg.combat.domain

import kotlin.math.roundToLong

class DamageCalculator(
		private val levelDifference: Int,
		private val reductionRate: Float = 0.5f,
		private val amplificationRate: Float = 1.5f
) {
	fun calculateEffectiveDamage(attackerLevel: Level, targetLevel: Level, damage: Long): Long = when {
		isDamageReductor(attackerLevel, targetLevel) -> damage * reductionRate
		isDamageAmplifier(attackerLevel, targetLevel) -> damage * amplificationRate
		else -> damage.toFloat()
	}.roundToLong()

	private fun isDamageAmplifier(attackerLevel: Level, targetLevel: Level)
			= attackerLevel.value.minus(levelDifference) >= targetLevel.value

	private fun isDamageReductor(attackerLevel: Level, targetLevel: Level)
			= attackerLevel.value.plus(levelDifference) <= targetLevel.value
}