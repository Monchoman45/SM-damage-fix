#!/usr/bin/python3

import sys

attack = {
	'cannon': {},
	'beam': {},
	'missile': {},
}
attack_strong = 1.5
attack_neutral = 1.0
attack_weak = 0.5
defense = {
	'shields': {},
	'armor': {},
	'systems': {},
}
defense_strong = 1.5
defense_neutral = 1.5
defense_weak = 0.0
effect_link_strength = 1.0
damage_multiplier = 3.0

formula = 'vanilla'

table_names = {
	'a_cannon': 'Cannons',
	'a_beam': 'Beams',
	'a_missile': 'Missiles',
	'a_default': 'Default',
	'a_heat': 'w/ Heat',
	'a_kinetic': 'w/ Kinetic',
	'a_em': 'w/ EM',
	'd_shields': 'vs. Shields',
	'd_armor': 'vs. Armor',
	'd_systems': 'vs. Systems',
	'c_shields': 'Shields',
	'c_armor': 'Armor',
	'c_systems': 'Systems',
}

def calc(at = attack, de = defense, mult = 0):
	if mult == 0: mult = damage_multiplier #apparently these are static

	result = {}
	for a in at:
		result[a] = {}
		for t in at[a]:
			result[a][t] = {}
			for d in de:
				if formula == 'vanilla':
					result[a][t][d] = (
						max(0.0, at[a][t]['heat'] - de[d]['heat'])
						+ max(0.0, at[a][t]['kinetic'] - de[d]['kinetic'])
						+ max(0.0, at[a][t]['em'] - de[d]['em'])
					) / 3.0 * mult
				elif formula == 'multiply':
					result[a][t][d] = (
						at[a][t]['heat'] * (1 - de[d]['heat'])
						+ at[a][t]['kinetic'] * (1 - de[d]['kinetic'])
						+ at[a][t]['em'] * (1 - de[d]['em'])
					) * mult
				else: result[a][t][d] = 0.0
	return result

def calc_to_table(calc, form = ''):
	if form == '': form = formula
	tab = []
	title_rows = {}
	row = 0
	d_header = ['']
	for d in defense: d_header.append(table_names['d_' + d])

	for a in calc:
		title_rows[len(tab)] = table_names['a_' + a].upper()
		tab.append([])
		tab.append(d_header)
		for t in calc[a]:
			r = [table_names['a_' + t]]
			for d in calc[a][t]:
				cell = ''
				if calc[a][t][d] >= 1: cell += '+'
				else: cell += '-'
				cell += str(round(abs((calc[a][t][d] - 1) * 100))) + '%';
				r.append(cell)
			tab.append(r)
		title_rows[len(tab)] = ''
		tab.append([])
	tab = tab[:-1]
	del title_rows[len(tab)]
	return table(tab, title_rows)

def table(t, title_rows):
	col_width = []
	title_width = -1
	col = 0 #not having for loops for this function is physically painful
	while col < len(t[1]):
		max = 2
		for i, row in enumerate(t):
			if i in title_rows: continue
			if len(row[col]) + 2 > max: max = len(row[col]) + 2
		col_width.append(max)
		title_width += max + 1
		col += 1

	v_spacer = '+'
	for i in col_width:
		j = 0
		while j < i:
			v_spacer += '-'
			j += 1
		v_spacer += '+'
	v_spacer += '\n'

	result = v_spacer
	for i, row in enumerate(t):
		if i in title_rows:
			result += '|'
			if (len(title_rows[i]) % 2 == 0 and title_width % 2 == 1) or (len(title_rows[i]) % 2 == 1 and title_width % 2 == 0):
				j = 0
				while j < (title_width - len(title_rows[i])) // 2 + 1:
					result += ' '
					j += 1
			else:
				j = 0
				while j < (title_width - len(title_rows[i])) // 2:
					result += ' '
					j += 1
			result += title_rows[i]
			j = 0
			while j < (title_width - len(title_rows[i])) // 2:
				result += ' '
				j += 1
			result += '|\n'
			
		else:
			result += '|'
			for col, cell in enumerate(row):
				result += ' ' + cell
				k = len(cell) + 1
				while k < col_width[col]:
					result += ' '
					k += 1
				result += '|'
			result += '\n'
		result += v_spacer
	result = result[:-1]
	return result

def calc_and_table(at = attack, de = defense, mult = 0): return calc_to_table(calc(at, de, mult))

def default_vanilla():
	global formula
	formula = 'vanilla'
	global attack_strong
	global attack_neutral
	global attack_weak
	attack_strong = 1.5
	attack_neutral = 1.0
	attack_weak = 0.5
	global defense_strong
	global defense_neutral
	global defense_weak
	defense_strong = 1.5
	defense_neutral = 1.5
	defense_weak = 0.0
	global damage_multiplier
	global effect_link_strength
	damage_multiplier = 3
	effect_link_strength = 1
	init()
def default_multiply():
	global formula
	formula = 'multiply'
	global attack_strong
	global attack_neutral
	global attack_weak
	attack_strong = 0.5
	attack_neutral = 0.33
	attack_weak = 0.17
	global defense_strong
	global defense_neutral
	global defense_weak
	defense_strong = 0.3
	defense_neutral = 0.3
	defense_weak = -0.6
	global damage_multiplier
	global effect_link_strength
	damage_multiplier = 1.0
	effect_link_strength = .34
	init()
def init():
	for i in attack: attack[i].clear()
	attack['cannon']['default'] = {'heat': attack_weak, 'kinetic': attack_strong, 'em': attack_neutral}
	attack['beam']['default'] = {'heat': attack_neutral, 'kinetic': attack_weak, 'em': attack_strong}
	attack['missile']['default'] = {'heat': attack_strong, 'kinetic': attack_neutral, 'em': attack_weak}
	els = effect_link_strength
	for i in attack:
		attack[i]['heat'] = {'heat': attack[i]['default']['heat'] + els, 'kinetic': attack[i]['default']['kinetic'] - (els / 2), 'em': attack[i]['default']['em'] - (els / 2)}
		attack[i]['kinetic'] = {'heat': attack[i]['default']['heat'] - (els / 2), 'kinetic': attack[i]['default']['kinetic'] + els, 'em': attack[i]['default']['em'] - (els / 2)}
		attack[i]['em'] = {'heat': attack[i]['default']['heat'] - (els / 2), 'kinetic': attack[i]['default']['kinetic'] - (els / 2), 'em': attack[i]['default']['em'] + els}

	defense.clear()
	defense['shields'] = {'heat': defense_strong, 'kinetic': defense_neutral, 'em': defense_weak}
	defense['armor'] = {'heat': defense_weak, 'kinetic': defense_strong, 'em': defense_neutral}
	defense['systems'] = {'heat': defense_neutral, 'kinetic': defense_weak, 'em': defense_strong}

if __name__ == '__main__':
	if len(sys.argv) == 1:
		print('Usage: python3 effect_tables.py <formula> [configs...]')
		print()
		print('Formulas:')
		print('\t`vanilla`: max(0, attack - defense) * damage')
		print('\t\tFormula used in current patch. Config changes made on this formula')
		print('\t\tcan be tested directly in game by simply editing blockBehaviorConfig.xml.')
		print('\t`mult`: attack * (1 - defense) * damage')
		print('\t\tProposed new formula that scales better with low resistances. To test')
		print('\t\tthese changes in game, you currently need to manually replace class files')
		print('\t\tin StarMade.jar.')
		print()
		print('Configs:')
		print('\t`attack_strong=val` or `as=val`:')
		print('\t\tSets the strongest effect percentage for all weapon types: kinetic on cannons,')
		print('\t\tEM on beams, and heat on missiles. Defaults:')
		print('\t\t\tvanilla: `attack_strong=1.5`')
		print('\t\t\tmult: `attack_strong=.5`')
		print('\t`attack_neutral=val` or `an=val`:')
		print('\t\tSets the neutral effect percentage for all weapon types: EM on cannons, heat')
		print('\t\ton beams, and kinetic on missiles. Defaults:')
		print('\t\t\tvanilla: `attack_neutral=1`')
		print('\t\t\tmult: `attack_neutral=.33`')
		print('\t`attack_weak=val` or `aw=val`:')
		print('\t\tSets the weakest effect percentage for all weapon types: heat on cannons,')
		print('\t\tkinetic on beams, and EM on missiles. Defaults:')
		print('\t\t\tvanilla: `attack_neutral=.5`')
		print('\t\t\tmult: `attack_neutral=.17`')
		print('\t`defense_strong=val` or `ds=val`:')
		print('\t\tSets the strongest effect resistance for all target types: heat on shields,')
		print('\t\tkinetic on armor, and EM on systems. Defaults:')
		print('\t\t\tvanilla: `defense_strong=1.5`')
		print('\t\t\tmult: `defense_strong=.3`')
		print('\t`defense_neutral=val` or `dn=val`:')
		print('\t\tSets the neutral effect resistance for all target types: kinetic on shields,')
		print('\t\tEM on armor, and heat on systems. Defaults:')
		print('\t\t\tvanilla: `defense_neutral=1.5`')
		print('\t\t\tmult: `defense_neutral=.3`')
		print('\t`defense_weak=val` or `dw=val`:')
		print('\t\tSets the weakest effect resistance for all target types: EM on shields, heat')
		print('\t\ton armor, and kinetic on systems. Defaults:')
		print('\t\t\tvanilla: `defense_weak=0`')
		print('\t\t\tmult: `defense_weak=-.6`')
		print('\t`damage_multiplier=val` or `mult=val`:')
		print('\t\tSets the base damage multiplier. Increasing the resistances of all targets')
		print('\t\twill naturally lower the output damage of all guns; this setting can be used')
		print('\t\tto counteract that effect. Defaults:')
		print('\t\t\tvanilla: `damage_multiplier=3`')
		print('\t\t\tmult: `damage_multiplier=1.0`')
		print('\t`effect_link_strength=val` or `els=val`:')
		print('\t\tSets how much linking an effect computer will change the attack spread by.')
		print('\t\tNote that there is no protection against negative attack values; make sure')
		print('\t\tthat the smallest effect in the default spread is at least `els / 2` to avoid')
		print('\t\tgenerating garbage data. Defaults:')
		print('\t\t\tvanilla: `effect_link_strength=1`')
		print('\t\t\tmult: `effect_link_strength=.34`')
		print()
		print('Examples:')
		print('\tpython3 effect_tables.py vanilla')
		print('\tpython3 effect_tables.py vanilla defense_strong=2 defense_neutral=1')
		print('\tpython3 effect_tables.py vanilla ds=2 dn=1')
		print('\tpython3 effect_tables.py vanilla as=2 an=.5 aw=.5 ds=1.5 dn=1 mult=2')
		print('\tpython3 effect_tables.py multiply as=.34 an=.33 aw=.33')
		sys.exit(0)

	formula = sys.argv[1]
	if formula == 'multiply': default_multiply()
	else: default_vanilla()
	if len(sys.argv) > 2:
		i = 2 #fuck you python give me for loops
		while i < len(sys.argv):
			spl = sys.argv[i].split('=')
			spl[0] = spl[0].strip()
			spl[1] = spl[1].strip()
			if spl[0] == 'attack_strong' or spl[0] == 'as': attack_strong = float(spl[1])
			elif spl[0] == 'attack_neutral' or spl[0] == 'an': attack_neutral = float(spl[1])
			elif spl[0] == 'attack_weak' or spl[0] == 'aw': attack_weak = float(spl[1])
			elif spl[0] == 'defense_strong' or spl[0] == 'ds': defense_strong = float(spl[1])
			elif spl[0] == 'defense_neutral' or spl[0] == 'dn': defense_neutral = float(spl[1])
			elif spl[0] == 'defense_weak' or spl[0] == 'dw': defense_weak = float(spl[1])
			elif spl[0] == 'damage_multiplier' or spl[0] == 'mult': damage_multiplier = float(spl[1])
			elif spl[0] == 'effect_link_strength' or spl[0] == 'els': effect_link_strength = float(spl[1])
			i += 1
	init()
	if formula == 'multiply': print('Formula: multiplication')
	else: print('Formula: vanilla')
	print()
	for a in attack: print(table_names['a_' + a] + ': [Heat: ' + str(attack[a]['default']['heat']) + ', Kinetic: ' + str(attack[a]['default']['kinetic']) + ', EM: ' + str(attack[a]['default']['em']) + ']')
	print()
	for d in defense: print(table_names['c_' + d] + ': [Heat: ' + str(defense[d]['heat']) + ', Kinetic: ' + str(defense[d]['kinetic']) + ', EM: ' + str(defense[d]['em']) + ']')
	print()
	print('Base damage multiplier: ' + str(damage_multiplier) + 'x')
	print('Effect link strength: ' + str(round(effect_link_strength, 2)))
	print()
	print(calc_and_table())

else: default_vanilla()
