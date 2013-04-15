for(i = 1; i < 100000; i++) {
	db.fubar.save({
		a: parseInt(Math.random() * 100000),
		b: parseInt(Math.random() * 100000),
		c: parseInt(Math.random() * 100000)
	});
}
db.fubar.ensureIndex({a:1, b:1})
db.fubar.ensureIndex({a:1, c:1})
db.fubar.ensureIndex({c:1})
db.fubar.ensureIndex({a:1, b:1, c:-1})
