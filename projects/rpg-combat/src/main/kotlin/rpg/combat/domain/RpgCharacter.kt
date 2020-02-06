package rpg.combat.domain

import rpg.combat.domain.Category.Melee
import rpg.combat.domain.Category.Ranged

data class Health(var value: Long) {

	fun minus(damage: Long) {
		this.value = maxOf(value - damage, 0)
	}

	fun plus(heal: Long) {
		this.value = if (value == 0L) value else minOf(heal + value, MAX_HEALTH)
	}

	companion object {
		const val MAX_HEALTH = 1000L
	}
}

data class Level(val value: Int)

sealed class Category {
	object Melee : Category()
	object Ranged : Category()
}

data class Position(val value: Int)

private const val DEFAULT_LEVEL_DIFFERENCE = 5

data class Faction(val name: String)

data class Factions(private val _factions: MutableSet<Faction> = mutableSetOf()) : Iterable<Faction> by _factions {

	fun add(faction: Faction) {
		_factions.add(faction)
	}

	fun remove(faction: Faction) {
		_factions.remove(faction)
	}
}

data class RpgCharacter constructor(
		val level: Level,
		val health: Health,
		val category: Category = Melee,
		val position: Position = Position(0),
		val factions: Factions = Factions(mutableSetOf()),
		private val damageCalculator: DamageCalculator = DamageCalculator(DEFAULT_LEVEL_DIFFERENCE)
) {

	init {
		require(health.value >= 0) {
			"Health value cannot be negative"
		}
		require(health.value <= Health.MAX_HEALTH) {
			"Health cannot be greater thatn ${Health.MAX_HEALTH}"
		}
		require(level.value > 0) {
			"Level initial value must be greater than 0"
		}
	}

	constructor() : this(Level(1), Health(Health.MAX_HEALTH))

	val isAlive: Boolean get() = health.value > 0

	fun attack(other: RpgCharacter, damage: Long, range: Float) {

		require(other !== this) {
			"Character cannot damage itself!"
		}

		require(this.isAlliedWith(other).not()) {
			"Allies cannot damage to each other!"
		}

		if (isValidRange(range)) {
			val effectiveDamage = damageCalculator.calculateEffectiveDamage(level, other.level, damage)
			other.health.minus(effectiveDamage)
		}

	}

	fun heal(other: RpgCharacter, heal: Long) {

		require(this.isAlliedWith(other)) {
			"Only allies can heal each other!"
		}

		health.plus(heal)
	}

	private fun isValidRange(range: Float) = when (category) {
		is Melee -> range in 0.0..2.0
		is Ranged -> range in 0.0..20.0
	}

	fun join(faction: Faction) {
		factions.add(faction)
	}

	fun leave(faction: Faction) {
		factions.remove(faction)
	}

	fun isAlliedWith(character: RpgCharacter) = factions.intersect(character.factions).isNotEmpty()

}
