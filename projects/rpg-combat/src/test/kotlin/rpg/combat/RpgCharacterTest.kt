package rpg.combat

import org.junit.Test
import rpg.combat.domain.Category.Ranged
import rpg.combat.domain.Faction
import rpg.combat.domain.Factions
import rpg.combat.domain.Health
import rpg.combat.domain.Level
import rpg.combat.domain.RpgCharacter
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class RpgCharacterTest {

	private lateinit var character: RpgCharacter
	private lateinit var character2: RpgCharacter
	private lateinit var character3: RpgCharacter

	private val factionOne: Faction = Faction("1")
	private val factionTwo: Faction = Faction("2")

	@Test
	fun `Melee character creation`() {

		givenANewCharacter()

		thenCharacterCompliesWithInitialStats()

	}

	@Test
	fun `character joins faction`() {
		givenANewCharacter()

		whenCharacterJoinsFaction()

		thenFactionIsAddedToCharacter()
	}

	@Test
	fun `character leaves faction`() {
		givenANewCharacter()
		givenTheCharacterJoinsFaction(character, factionOne)

		whenCharacterLeavesFaction()

		thenCharacterCompliesWithInitialStats()
	}

	@Test
	fun `characters in same faction are allies`() {
		givenTwoCharacters()
		givenTheCharacterJoinsFaction(character, factionOne)
		givenTheCharacterJoinsFaction(character2, factionOne)

		// when

		thenCharactersAreAllies()

	}

	@Test(expected = IllegalArgumentException::class)
	fun `character is damaged by Melee from same faction`() {
		givenTwoCharacters()
		givenTheCharacterJoinsFaction(character, factionOne)
		givenTheCharacterJoinsFaction(character2, factionOne)

		whenFirstAttacksSecondWith(100L, 2f)

		thenSecondWasDamaged()
	}

	@Test
	fun `character is damaged by Melee from other faction`() {
		givenTwoCharacters()
		givenTheCharacterJoinsFaction(character, factionOne)
		givenTheCharacterJoinsFaction(character2, factionTwo)

		whenFirstAttacksSecondWith(100L, 2f)

		thenSecondWasDamaged()
	}

	@Test
	fun `character is healed by other from same faction`() {
		givenThreeCharacters()
		givenTheCharacterJoinsFaction(character, factionOne)
		givenTheCharacterJoinsFaction(character2, factionOne)
		givenTheCharacterJoinsFaction(character3, factionTwo)

		whenCharacterAttacksOtherWith(character3, character2, 100, 2f)
		whenCharacterIsHealedBy(character2, character, 100)

		thenSecondsHeatlhIsMaxed()
	}

	@Test(expected = IllegalArgumentException::class)
	fun `character is healed by other from different faction`() {
		givenThreeCharacters()
		givenTheCharacterJoinsFaction(character, factionOne)
		givenTheCharacterJoinsFaction(character2, factionOne)
		givenTheCharacterJoinsFaction(character3, factionTwo)

		whenCharacterAttacksOtherWith(character3, character2, 100, 2f)
		whenCharacterIsHealedBy(character2, character3, 100)

		thenSecondsHeatlhIsMaxed()
	}

	@Test
	fun `character can´t be damaged by Melee when out of range`() {
		givenTwoCharacters()

		whenFirstAttacksSecondWith(100L, 10f)

		thenSecondHasFullHealth()
	}

	@Test
	fun `character is killed by Melee`() {
		givenTwoCharacters()

		whenFirstAttacksSecondWith(1000L, 2f)

		thenSecondIsDead()
	}

	@Test
	fun `character is killed by Ranged`() {
		givenFirstCharacterIsFiveLevelsAhead()

		whenFirstAttacksSecondWith(1000L, 20f)

		thenSecondIsDead()
	}

	@Test
	fun `character can't be killed by Ranged when out of range`() {
		givenFirstCharacterIsFiveLevelsAhead()

		whenFirstAttacksSecondWith(1000L, 21f)

		thenSecondHasFullHealth()
	}

	@Test
	fun `character overkill`() {
		givenTwoCharacters()

		whenFirstAttacksSecondWith(9999L, 2f)

		thenSecondsHealthIsZero()
	}



	@Test(expected = IllegalArgumentException::class)
	fun `characters can't commit suicide`() {
		givenANewCharacter()
		whenCharacterAttacksItself()
		thenNothingHappens()
	}

	@Test
	fun `dealing reduced damage`() {
		givenSecondCharacterIsFiveLevelsAhead()
		whenFirstAttacksSecondWith(100L, range = 2f)
		thenSecondWasDamaged(50L)
	}

	@Test
	fun `dealing amplified damage`() {
		givenFirstCharacterIsFiveLevelsAhead()
		whenFirstAttacksSecondWith(100L, 2f)
		thenSecondWasDamaged(150L)
	}

	private fun givenTheCharacterJoinsFaction(character: RpgCharacter, faction: Faction) {
		character.join(faction)
	}

	private fun whenCharacterLeavesFaction() {
		character.leave(factionOne)
	}

	private fun whenCharacterJoinsFaction() {
		character.join(Faction("1"))
	}

	private fun thenFactionIsAddedToCharacter() {
		assertEquals(Factions(mutableSetOf(Faction("1"))), character.factions)
	}

	private fun givenSecondCharacterIsFiveLevelsAhead() {
		character = RpgCharacter(Level(1), Health(Health.MAX_HEALTH))
		character2 = RpgCharacter(Level(6), Health(Health.MAX_HEALTH))
	}

	private fun givenFirstCharacterIsFiveLevelsAhead() {
		character = RpgCharacter(Level(6), Health(Health.MAX_HEALTH), Ranged)
		character2 = RpgCharacter(Level(1), Health(Health.MAX_HEALTH))
	}

	private fun givenTwoCharacters() {
		character = RpgCharacter()
		character2 = RpgCharacter()
	}

	private fun givenThreeCharacters() {
		givenTwoCharacters()
		character3 = RpgCharacter()
	}

	private fun givenANewCharacter() {
		character = RpgCharacter()
	}

	private fun thenNothingHappens() = assert(character.health.value == 1000L)

	private fun whenCharacterAttacksItself() = character.attack(character, 10L, 2f)

	private fun thenSecondsHeatlhIsMaxed() {
		assert(character2.health == Health(Health.MAX_HEALTH))
	}

	private fun whenCharacterAttacksOtherWith(attacker: RpgCharacter, victim: RpgCharacter, damage: Long, range: Float) {
		attacker.attack(victim, damage, range)
	}

	private fun whenCharacterIsHealedBy(damagedCharacter: RpgCharacter, healer: RpgCharacter, healValue: Long) {
		damagedCharacter.heal(healer, healValue)
	}

	private fun thenSecondsHealthIsZero() {
		assert(character2.health == Health(0))
	}

	private fun thenSecondIsDead() {
		assertFalse(character2.isAlive)
	}

	private fun thenSecondWasDamaged() {
		assert(character2.health == Health(Health.MAX_HEALTH - 100))
	}

	private fun thenSecondWasDamaged(damage: Long) {
		assert(character2.health == Health(Health.MAX_HEALTH - damage))
	}

	private fun thenSecondHasFullHealth() {
		assert(character2.health == Health(Health.MAX_HEALTH))
	}

	private fun whenFirstAttacksSecondWith(damage: Long, range: Float) {
		character.attack(character2, damage, range)
	}

	private fun thenCharacterCompliesWithInitialStats() {
		assert(character.factions.toList().isEmpty())
		assert(character.isAlive)
		assert(character.level == Level(1))
		assert(character.health == Health(1000L))
	}

	private fun thenCharactersAreAllies() {
		character.isAlliedWith(character2)
	}

}