package rpg.combat

import org.junit.Test
import rpg.combat.domain.Category.Ranged
import rpg.combat.domain.Health
import rpg.combat.domain.Level
import rpg.combat.domain.RpgCharacter
import kotlin.test.assertFalse

class RpgCharacterTest {

	private lateinit var character: RpgCharacter
	private lateinit var character2: RpgCharacter

	@Test
	fun `Melee character creation`(){

		givenANewCharacter()

		thenCharacterCompliesWithInitialStats()

	}

	@Test
	fun `character is damaged by Melee`() {
		givenTwoCharacters()

		whenFirstAttacksSecondWith(100L, 2f)

		thenSecondWasDamaged()
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

	@Test
	fun `character is healed`() {
		givenTwoCharacters()
		givenFirstAttacksSecondWith(100, 2f)

		whenSecondHeals(100L)

		thenSecondIsHealed()
	}

	@Test
	fun `character overhealed`() {
		givenTwoCharacters()
		givenFirstAttacksSecondWith(100, 2f)

		whenSecondHeals(999)

		thenSecondsHeatlhIsMaxed()
	}

	@Test
	fun `characters can't be resurrected`() {
		givenTwoCharacters()
		givenFirstAttacksSecondWith(1000, 2f)

		whenSecondHeals(100)

		thenSecondIsDead()
	}

	@Test(expected = IllegalArgumentException::class)
	fun `characters can't commit suicide`() {
		givenANewCharacter()
		whenCharacterAttacksItself()
		thenNothingHappens()
	}

	@Test
	fun `dealing reduced damage`(){
		givenSecondCharacterIsFiveLevelsAhead()
		whenFirstAttacksSecondWith(100L, range = 2f)
		thenSecondWasDamaged(50L)
	}

	@Test
	fun `dealing amplified damage`(){
		givenFirstCharacterIsFiveLevelsAhead()
		whenFirstAttacksSecondWith(100L, 2f)
		thenSecondWasDamaged(150L)
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

	private fun thenNothingHappens() = assert(character.health.value == 1000L)

	private fun whenCharacterAttacksItself() = character.attack(character, 10L, 2f)

	private fun thenSecondsHeatlhIsMaxed() {
		assert(character2.health == Health(Health.MAX_HEALTH))
	}

	private fun thenSecondIsHealed() {
		assert(character2.health == Health(Health.MAX_HEALTH))
	}

	private fun whenSecondHeals(heal: Long) {
		character2.heal(heal)
	}

	private fun givenFirstAttacksSecondWith(damage: Long, range: Float) {
		character.attack(character2, damage, range)
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
		assert(character.isAlive)
		assert(character.level == Level(1))
		assert(character.health == Health(1000L))
	}



	private fun givenANewCharacter() {
		character = RpgCharacter()
	}
}