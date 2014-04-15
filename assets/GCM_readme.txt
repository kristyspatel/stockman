
PICK_WORD
ACCEPT
DECLINE
	id_player
	id_game

COMMENT
	id_user
	id_notification
	text
	id_comment

ACCEPT_FRIEND
CANCEL_FRIEND
	id_user1
	id_user2

CREATE_GAME
	name
	members
	id_game
	id_players

GUESS
	me
		id_player
		status
		die_date
	him
		id_player
		status
		die_date
	letter
	correct
	id_game

BUY
SELL
	id_company
	id_stock
	id_player
	amount
	price
	id_game
	new_amount (for SELL only)

Test
	