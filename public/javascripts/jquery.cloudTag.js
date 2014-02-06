/*!
 jQuery.awesomeCloud v0.2 indyarmy.com
 by Russ Porosky
 IndyArmy Network, Inc.

 Usage:
 $( "#myContainer" ).awesomeCloud( settings );

 Your container must contain words in the following format:
 <element data-weight="12">Word</element>
 The <element> can be any valid HTML element (for example, <span>), and
 the weight must be a decimal or integer contained in the "data-weight"
 attribute. The content of the <element> is the word that will be
 displayed. The original element is removed from the page (but not the DOM).

 Settings:
 "size" {
 "grid" : 8, // word spacing; smaller is more tightly packed but takes longer
 "factor" : 0, // font resizing factor; default "0" means automatically fill the container
 "normalize" : true // reduces outlier weights for a more attractive output
 },
 "color" {
 "background" : "rgba(255,255,255,0)", // default is transparent
 "start" : "#20f", // color of the smallest font
 "end" : "#e00" // color of the largest font
 },
 "options" {
 "color" : "gradient", // if set to "random-light" or "random-dark", color.start and color.end are ignored
 "rotationRatio" : 0.3, // 0 is all horizontal words, 1 is all vertical words
 "printMultiplier" : 1 // 1 will look best on screen and is fastest; setting to 3.5 gives nice 300dpi printer output but takes longer
 },
 "font" : "Futura, Helvetica, sans-serif", // font family, identical to CSS font-family attribute
 "shape" : "circle", // one of "circle", "square", "diamond", "triangle", "triangle-forward", "x", "pentagon" or "star"; this can also be a function with the following prototype - function( theta ) {}

 Notes:
 AwesomeCloud uses the HTML5 canvas element to create word clouds
 similar to http://wordle.net/. It may or may not work for you.

 If your words are all fairly evenly weighted and are large compared to
 the containing element, you may need to adjust the size.grid setting
 to make the output more attractive. Conversely, you can adjust the
 size.factor setting instead.

 It should be noted that the more words you have, the smaller the size.grid,
 and the larger the options.printMultiplier, the longer it will take to
 generate and display the word cloud.

 Extra Thanks:
 Without Timothy Chien's work (https://github.com/timdream/wordcloud),
 this plugin would have taken much longer and been much uglier. Fate
 smiled and I found his version while I was searching out the equations
 I needed to make a circle-shaped cloud. I've simplified and, in places,
 dumbified his code for this plugin, and even outright copied chunks of
 it since those parts just worked far better than what I had originally
 written. Many thanks, Timothy, for saving some of my wits, sanity and
 hair over the past week.

 Thanks to http://www.websanova.com/tutorials/jquery/jquery-plugin-development-boilerplate
 for providing a great boilerplate I could use for my first jQuery plugin.
 My original layout worked, but this one was much better.
 */
(function (e) {
    "use strict";
    function r(e, t) {
        this.bctx = null;
        this.bgPixel = null;
        this.ctx = null;
        this.diffChannel = null;
        this.container = t;
        this.grid = [];
        this.ngx = null;
        this.ngy = null;
        this.settings = e;
        this.size = null;
        this.words = [];
        this.linkTable = [];
        this.match = null;
        return this
    }

    var t = "awesomeCloud", n = {size: {grid: 8, factor: 0, normalize: true}, color: {background: "rgba(255,255,255,0)", start: "#20f", end: "#e00"}, options: {color: "gradient", rotationRatio: .3, printMultiplier: 1, sort: "highest"}, font: "Helvetica, Arial, sans-serif", shape: "circle"};
    e.fn.awesomeCloud = function (i, s) {
        if (typeof i === "object") {
            s = i
        } else if (typeof i === "string") {
            var o = this.data("_" + t);
            if (o) {
                if (n[i] !== undefined) {
                    if (s !== undefined) {
                        o.settings[i] = s;
                        return true
                    } else {
                        return o.settings[i]
                    }
                } else {
                    return false
                }
            } else {
                return false
            }
        }
        s = e.extend(true, {}, n, s || {});
        return this.each(function () {
            var n = e(this), i = jQuery.extend(true, {}, s), o = new r(i, n);
            o.create();
            n.data("_" + t, o)
        })
    };
    r.prototype = {create: function () {
        var n = this, r = 0, i = null, s = 0, o = 0, u = t + "TempCheck", a = null, f = false, l = 0, c = .1, h = 0, p = 0, d = 0, v = 0, m = null, g = null, y = null, b, w, E, S, x;
        this.settings.weightFactor = function (e) {
            return e * n.settings.size.factor
        };
        this.settings.gridSize = Math.max(this.settings.size.grid, 4) * this.settings.options.printMultiplier;
        this.settings.color.start = this.colorToRGBA(this.settings.color.start);
        this.settings.color.end = this.colorToRGBA(this.settings.color.end);
        this.settings.color.background = this.colorToRGBA(this.settings.color.background);
        this.settings.minSize = this.minimumFontSize();
        this.settings.ellipticity = 1;
        switch (this.settings.shape) {
            case"square":
                this.settings.shape = function (e) {
                    var t = (e + Math.PI / 4) % (2 * Math.PI / 4);
                    return 1 / (Math.cos(t) + Math.sin(t))
                };
                break;
            case"diamond":
                this.settings.shape = function (e) {
                    var t = e % (2 * Math.PI / 4);
                    return 1 / (Math.cos(t) + Math.sin(t))
                };
                break;
            case"x":
                this.settings.shape = function (e) {
                    var t = e % (2 * Math.PI / 4);
                    return 1 / (Math.cos(t) + Math.sin(t) - 2 * Math.PI / 4)
                };
                break;
            case"triangle":
                this.settings.shape = function (e) {
                    var t = (e + Math.PI * 3 / 2) % (2 * Math.PI / 3);
                    return 1 / (Math.cos(t) + Math.sqrt(3) * Math.sin(t))
                };
                break;
            case"triangle-forward":
                this.settings.shape = function (e) {
                    var t = e % (2 * Math.PI / 3);
                    return 1 / (Math.cos(t) + Math.sqrt(3) * Math.sin(t))
                };
                break;
            case"pentagon":
                this.settings.shape = function (e) {
                    var t = (e + .955) % (2 * Math.PI / 5);
                    return 1 / (Math.cos(t) + .726543 * Math.sin(t))
                };
                break;
            case"star":
                this.settings.shape = function (e) {
                    var t = (e + .955) % (2 * Math.PI / 10);
                    if ((e + .955) % (2 * Math.PI / 5) - 2 * Math.PI / 10 >= 0) {
                        return 1 / (Math.cos(2 * Math.PI / 10 - t) + 3.07768 * Math.sin(2 * Math.PI / 10 - t))
                    } else {
                        return 1 / (Math.cos(t) + 3.07768 * Math.sin(t))
                    }
                };
                break;
            case"circle":
                this.settings.shape = function (e) {
                    return 1
                };
                break;
            default:
                this.settings.shape = function (e) {
                    return 1
                };
                break
        }
        this.size = {left: this.container.offset().left, top: this.container.offset().top, height: this.container.height() * this.settings.options.printMultiplier, width: this.container.width() * this.settings.options.printMultiplier, screenHeight: this.container.height(), screenWidth: this.container.width()};
        this.settings.ellipticity = this.size.height / this.size.width;
        if (this.settings.ellipticity > 2) {
            this.settings.ellipticity = 2
        }
        if (this.settings.ellipticity < .2) {
            this.settings.ellipticity = .2
        }
        this.settings.weight = {lowest: null, highest: null, average: null};
        this.container.children().each(function (t, r) {
            m = null;
            y = null;
            o = parseInt(e(this).attr("data-weight"), 10);
            s += o;
            if (!n.settings.weight.lowest) {
                n.settings.weight.lowest = o
            }
            if (!n.settings.weight.highest) {
                n.settings.weight.highest = o
            }
            if (o < n.settings.weight.lowest) {
                n.settings.weight.lowest = o
            }
            if (o > n.settings.weight.highest) {
                n.settings.weight.highest = o
            }
            n.settings.weight.average = s / (t + 1);
            e(this).css("display", "none");
            if (e(this).has("a").length === 0) {
                y = e(this).html()
            } else {
                var i = e(this).children(":first");
                m = i.attr("href");
                g = i.attr("target");
                y = i.html()
            }
            n.words.push([y, o, m, g])
        });
        this.settings.range = this.settings.weight.highest - this.settings.weight.lowest;
        if (this.settings.size.normalize === true) {
            this.words.sort(function (e, t) {
                return e[1] - t[1]
            });
            for (r = 0; r < this.words.length; r++) {
                if (a === null) {
                    a = this.words[r][1]
                } else {
                    if (this.words[r][1] - a > this.settings.weight.average) {
                        this.words[r][1] -= (this.words[r][1] - a) / (this.settings.weight.average * .38) + a
                    }
                }
            }
        }
        this.words.sort(function (e, t) {
            if (n.settings.options.sort === "random") {
                return.5 - Math.random()
            } else if (n.settings.options.sort === "lowest") {
                return e[1] - t[1]
            } else {
                return t[1] - e[1]
            }
        });
        if (this.settings.size.factor === parseInt(0, 10)) {
            this.settings.size.factor = 1;
            i = t + "SizeTest";
            f = false;
            l = 0;
            c = .1;
            h = 0;
            p = 0;
            d = 0;
            v = 0;
            b = Math.min(this.size.width, this.size.height);
            w = this.createCanvas({id: i, width: b, height: b, left: 0, top: 0});
            for (r = 0; r < this.words.length; r++) {
                w.font = this.settings.weightFactor(this.words[r][1]) + "px " + this.settings.font;
                v = w.measureText(this.words[r][0]).width;
                if (v > h) {
                    h = v;
                    p = this.words[r]
                }
            }
            while (!f) {
                l = this.settings.weightFactor(p[1]);
                w.font = l.toString(10) + "px " + this.settings.font;
                v = w.measureText(p[0]).width;
                if (v > b * .95) {
                    this.settings.size.factor -= c
                } else if (v < b * .9) {
                    this.settings.size.factor += c
                } else {
                    f = true
                }
                d += 1;
                if (d > 1e4) {
                    f = true
                }
            }
            this.destroyCanvas(i);
            this.settings.size.factor -= c
        }
        this.settings.color.increment = {r: (this.settings.color.end.r - this.settings.color.start.r) / this.settings.range, g: (this.settings.color.end.g - this.settings.color.start.g) / this.settings.range, b: (this.settings.color.end.b - this.settings.color.start.b) / this.settings.range, a: (this.settings.color.end.a - this.settings.color.start.a) / this.settings.range};
        this.ngx = Math.floor(this.size.width / this.settings.gridSize);
        this.ngy = Math.floor(this.size.height / this.settings.gridSize);
        this.grid = [];
        i = t + this.container.attr("id");
        this.ctx = this.createCanvas({parent: this.container, id: i, width: this.size.width, height: this.size.height, left: "0px", top: "0px"});
        this.bctx = this.createCanvas({id: u, width: 1, height: 1, left: 0, top: 0});
        this.bctx.fillStyle = this.settings.color.background.rgba;
        this.bctx.clearRect(0, 0, 1, 1);
        this.bctx.fillStyle = this.settings.color.background.rgba;
        this.bctx.fillRect(0, 0, 1, 1);
        this.bgPixel = this.bctx.getImageData(0, 0, 1, 1).data;
        if (typeof this.settings.options.color !== "function" && this.settings.options.color.substr(0, 6) !== "random" && this.settings.options.color.substr(0, 8) !== "gradient") {
            this.bctx.fillStyle = this.colorToRGBA(this.settings.options.color).rgba;
            this.bctx.fillRect(0, 0, 1, 1);
            E = this.bctx.getImageData(0, 0, 1, 1).data;
            r = 4;
            while (r--) {
                if (Math.abs(E[r] - this.bgPixel[r]) > 10) {
                    this.diffChannel = r;
                    break
                }
            }
        } else {
            this.diffChannel = NaN
        }
        this.destroyCanvas(u);
        S = this.ngx;
        while (S--) {
            this.grid[S] = [];
            x = this.ngy;
            while (x--) {
                this.grid[S][x] = true
            }
        }
        this.ctx.fillStyle = this.settings.color.background.rgba;
        this.ctx.clearRect(0, 0, this.ngx * (this.settings.gridSize + 1), this.ngy * (this.settings.gridSize + 1));
        this.ctx.fillRect(0, 0, this.ngx * (this.settings.gridSize + 1), this.ngy * (this.settings.gridSize + 1));
        this.ctx.textBaseline = "top";
        r = 0;
        window.setImmediate(function T() {
            if (r >= n.words.length) {
                return
            }
            n.putWord(n.words[r][0], n.words[r][1], n.words[r][2], n.words[r][3]);
            r += 1;
            window.setImmediate(T)
        });
        n.allDone(i);
        return true
    }, allDone: function (t) {
        var n = this, r = document.getElementById(t);
        e("#" + t).width(this.size.screenWidth);
        e("#" + t).height(this.size.screenHeight);
        e("#" + t).css("display", "block");
        e("#" + t).css("visibility", "visible");
        r.addEventListener("mousemove", function (t) {
            var i = 0, s = 0;
            if (t.layerX || t.layerX === 0) {
                i = t.layerX;
                s = t.layerY
            }
            i -= r.offsetLeft;
            i += e(r).position().left;
            i = Math.floor(i * n.settings.options.printMultiplier);
            s -= r.offsetTop;
            s += e(r).position().top;
            s = Math.floor(s * n.settings.options.printMultiplier);
            n.match = null;
            for (var o = 0, u = n.linkTable.length; o < u; o++) {
                var a = n.linkTable[o];
                if (i >= a.x && i <= a.x + a.width && s >= a.y && s <= a.y + a.height) {
                    n.match = a
                }
            }
            if (n.match !== null) {
                document.body.style.cursor = "pointer"
            } else {
                document.body.style.cursor = ""
            }
        }, false);
        r.addEventListener("click", function (e) {
            if (n.match !== null) {
                if (n.match.target) {
                    window.open(n.match.link, n.match.target)
                } else {
                    window.location = n.match.link
                }
            }
        }, false)
    }, minimumFontSize: function () {
        var e = t + "FontTest", n = this.createCanvas({id: e, width: 50, height: 50, left: 0, top: 0}), r = 20, i, s;
        while (r) {
            n.font = r.toString(10) + "px sans-serif";
            if (n.measureText("Ｗ").width === i && n.measureText("m").width === s) {
                this.destroyCanvas(e);
                return(r + 1) / 2
            }
            i = n.measureText("Ｗ").width;
            s = n.measureText("m").width;
            r -= 1
        }
        this.destroyCanvas(e);
        return 0
    }, createCanvas: function (t) {
        var n = t.id, r, i = e("body");
        if (t.parent !== undefined) {
            i = t.parent
        }
        i.append('<canvas id="' + n + '" width="' + t.width + '" height="' + t.height + '">.</canvas>');
        e("#" + n).css("visibility", "hidden");
        e("#" + n).css("display", "none");
        e("#" + n).css("position", "relative");
        e("#" + n).css("z-index", 1e4);
        e("#" + n).width(t.width);
        e("#" + n).height(t.height);
        e("#" + n).offset({top: t.top, left: t.left});
        r = document.getElementById(n);
        r.setAttribute("width", t.width);
        r.setAttribute("height", t.height);
        return r.getContext("2d")
    }, destroyCanvas: function (t) {
        e("#" + t).remove()
    }, putWord: function (e, n, r, i) {
        var s = this, o = Math.random() < this.settings.options.rotationRatio, u = this.settings.weightFactor(n), a = null, f = null, l, c, h, p, d = {}, v, m, g, y, b, w, E, S, x, T, N, C, k, L, A;
        if (u <= this.settings.minSize) {
            return false
        }
        this.ctx.font = u.toString(10) + "px " + this.settings.font;
        if (o) {
            a = this.ctx.measureText(e).width;
            f = Math.max(u, this.ctx.measureText("m").width, this.ctx.measureText("Ｗ").width);
            if (/[Jgpqy]/.test(e)) {
                f *= 3 / 2
            }
            f += Math.floor(u / 6);
            a += Math.floor(u / 6)
        } else {
            f = this.ctx.measureText(e).width;
            a = Math.max(u, this.ctx.measureText("m").width, this.ctx.measureText("Ｗ").width);
            if (/[Jgpqy]/.test(e)) {
                a *= 3 / 2
            }
            a += Math.floor(u / 6);
            f += Math.floor(u / 6)
        }
        f = Math.ceil(f);
        a = Math.ceil(a);
        y = Math.ceil(f / this.settings.gridSize);
        b = Math.ceil(a / this.settings.gridSize);
        w = [this.ngx / 2, this.ngy / 2];
        E = Math.floor(Math.sqrt(this.ngx * this.ngx + this.ngy * this.ngy));
        S = this.ngx + this.ngy;
        x = E + 1;
        while (x--) {
            T = S;
            A = [];
            while (T--) {
                N = this.settings.shape(T / S * 2 * Math.PI);
                A.push([Math.floor(w[0] + (E - x) * N * Math.cos(-T / S * 2 * Math.PI) - y / 2), Math.floor(w[1] + (E - x) * N * this.settings.ellipticity * Math.sin(-T / S * 2 * Math.PI) - b / 2), T / S * 2 * Math.PI])
            }
            if (A.shuffle().some(function (l) {
                if (s.canFitText(l[0], l[1], y, b)) {
                    m = Math.floor(l[0] * s.settings.gridSize + (y * s.settings.gridSize - f) / 2);
                    g = Math.floor(l[1] * s.settings.gridSize + (b * s.settings.gridSize - a) / 2);
                    if (o) {
                        L = t + "Rotator";
                        k = s.createCanvas({id: L, width: f, height: a, left: 0, top: 0});
                        C = document.getElementById(L);
                        k.fillStyle = s.settings.color.background.rgba;
                        k.fillRect(0, 0, f, a);
                        k.fillStyle = s.wordcolor(e, n, u, E - x, l[2]);
                        k.font = u.toString(10) + "px " + s.settings.font;
                        k.textBaseline = "top";
                        if (o) {
                            k.translate(0, a);
                            k.rotate(-Math.PI / 2)
                        }
                        k.fillText(e, Math.floor(u / 6), 0);
                        s.ctx.clearRect(m, g, f, a);
                        s.ctx.drawImage(C, m, g, f, a);
                        s.destroyCanvas(L)
                    } else {
                        v = u.toString(10) + "px " + s.settings.font;
                        s.ctx.font = v;
                        s.ctx.fillStyle = s.wordcolor(e, n, u, E - x, l[2]);
                        s.ctx.fillText(e, m, g);
                        a = s.getTextHeight(v).height;
                        f = s.ctx.measureText(e).width
                    }
                    if (r !== null) {
                        s.linkTable.push({x: m, y: g, width: f, height: a, word: e, link: r, target: i})
                    }
                    s.updateGrid(l[0], l[1], y, b);
                    return true
                }
                return false
            })) {
                return true
            }
        }
        return false
    }, canFitText: function (e, t, n, r) {
        if (e < 0 || t < 0 || e + n > this.ngx || t + r > this.ngy) {
            return false
        }
        var i = n, s;
        while (i--) {
            s = r;
            while (s--) {
                if (!this.grid[e + i][t + s]) {
                    return false
                }
            }
        }
        return true
    }, wordcolor: function (e, t, n, r, i) {
        var s = null;
        switch (this.settings.options.color) {
            case"gradient":
                s = "rgba(" + Math.round(this.settings.color.start.r + this.settings.color.increment.r * (t - this.settings.weight.lowest)) + "," + Math.round(this.settings.color.start.g + this.settings.color.increment.g * (t - this.settings.weight.lowest)) + "," + Math.round(this.settings.color.start.b + this.settings.color.increment.b * (t - this.settings.weight.lowest)) + "," + Math.round(this.settings.color.start.a + this.settings.color.increment.a * (t - this.settings.weight.lowest)) + ")";
                break;
            case"random-dark":
                s = "rgba(" + Math.floor(Math.random() * 128).toString(10) + "," + Math.floor(Math.random() * 128).toString(10) + "," + Math.floor(Math.random() * 128).toString(10) + ",1)";
                break;
            case"random-light":
                s = "rgba(" + Math.floor(Math.random() * 128 + 128).toString(10) + "," + Math.floor(Math.random() * 128 + 128).toString(10) + "," + Math.floor(Math.random() * 128 + 128).toString(10) + ",1)";
                break;
            default:
                if (typeof this.settings.wordColor !== "function") {
                    s = "rgba(127,127,127,1)"
                } else {
                    s = this.settings.wordColor(e, t, n, r, i)
                }
                break
        }
        return s
    }, updateGrid: function (e, t, n, r, i) {
        var s = n, o, u = this.ctx.getImageData(e * this.settings.gridSize, t * this.settings.gridSize, n * this.settings.gridSize, r * this.settings.gridSize);
        while (s--) {
            o = r;
            while (o--) {
                if (!this.isGridEmpty(u, s * this.settings.gridSize, o * this.settings.gridSize, n * this.settings.gridSize, r * this.settings.gridSize, i)) {
                    this.grid[e + s][t + o] = false
                }
            }
        }
    }, isGridEmpty: function (e, t, n, r, i, s) {
        var o = this.settings.gridSize, u, a;
        if (!isNaN(this.diffChannel) && !s) {
            while (o--) {
                u = this.settings.gridSize;
                while (u--) {
                    if (this.getChannelData(e.data, t + o, n + u, r, i, this.diffChannel) !== this.bgPixel[this.diffChannel]) {
                        return false
                    }
                }
            }
        } else {
            while (o--) {
                u = this.settings.gridSize;
                while (u--) {
                    a = 4;
                    while (a--) {
                        if (this.getChannelData(e.data, t + o, n + u, r, i, a) !== this.bgPixel[a]) {
                            return false
                        }
                    }
                }
            }
        }
        return true
    }, getChannelData: function (e, t, n, r, i, s) {
        return e[(n * r + t) * 4 + s]
    }, colorToRGBA: function (e) {
        e = e.replace(/^\s*#|\s*$/g, "");
        if (e.length === 3) {
            e = e.replace(/(.)/g, "$1$1")
        }
        e = e.toLowerCase();
        var t = {aliceblue: "f0f8ff", antiquewhite: "faebd7", aqua: "00ffff", aquamarine: "7fffd4", azure: "f0ffff", beige: "f5f5dc", bisque: "ffe4c4", black: "000000", blanchedalmond: "ffebcd", blue: "0000ff", blueviolet: "8a2be2", brown: "a52a2a", burlywood: "deb887", cadetblue: "5f9ea0", chartreuse: "7fff00", chocolate: "d2691e", coral: "ff7f50", cornflowerblue: "6495ed", cornsilk: "fff8dc", crimson: "dc143c", cyan: "00ffff", darkblue: "00008b", darkcyan: "008b8b", darkgoldenrod: "b8860b", darkgray: "a9a9a9", darkgreen: "006400", darkkhaki: "bdb76b", darkmagenta: "8b008b", darkolivegreen: "556b2f", darkorange: "ff8c00", darkorchid: "9932cc", darkred: "8b0000", darksalmon: "e9967a", darkseagreen: "8fbc8f", darkslateblue: "483d8b", darkslategray: "2f4f4f", darkturquoise: "00ced1", darkviolet: "9400d3", deeppink: "ff1493", deepskyblue: "00bfff", dimgray: "696969", dodgerblue: "1e90ff", feldspar: "d19275", firebrick: "b22222", floralwhite: "fffaf0", forestgreen: "228b22", fuchsia: "ff00ff", gainsboro: "dcdcdc", ghostwhite: "f8f8ff", gold: "ffd700", goldenrod: "daa520", gray: "808080", green: "008000", greenyellow: "adff2f", honeydew: "f0fff0", hotpink: "ff69b4", indianred: "cd5c5c", indigo: "4b0082", ivory: "fffff0", khaki: "f0e68c", lavender: "e6e6fa", lavenderblush: "fff0f5", lawngreen: "7cfc00", lemonchiffon: "fffacd", lightblue: "add8e6", lightcoral: "f08080", lightcyan: "e0ffff", lightgoldenrodyellow: "fafad2", lightgrey: "d3d3d3", lightgreen: "90ee90", lightpink: "ffb6c1", lightsalmon: "ffa07a", lightseagreen: "20b2aa", lightskyblue: "87cefa", lightslateblue: "8470ff", lightslategray: "778899", lightsteelblue: "b0c4de", lightyellow: "ffffe0", lime: "00ff00", limegreen: "32cd32", linen: "faf0e6", magenta: "ff00ff", maroon: "800000", mediumaquamarine: "66cdaa", mediumblue: "0000cd", mediumorchid: "ba55d3", mediumpurple: "9370d8", mediumseagreen: "3cb371", mediumslateblue: "7b68ee", mediumspringgreen: "00fa9a", mediumturquoise: "48d1cc", mediumvioletred: "c71585", midnightblue: "191970", mintcream: "f5fffa", mistyrose: "ffe4e1", moccasin: "ffe4b5", navajowhite: "ffdead", navy: "000080", oldlace: "fdf5e6", olive: "808000", olivedrab: "6b8e23", orange: "ffa500", orangered: "ff4500", orchid: "da70d6", palegoldenrod: "eee8aa", palegreen: "98fb98", paleturquoise: "afeeee", palevioletred: "d87093", papayawhip: "ffefd5", peachpuff: "ffdab9", peru: "cd853f", pink: "ffc0cb", plum: "dda0dd", powderblue: "b0e0e6", purple: "800080", red: "ff0000", rosybrown: "bc8f8f", royalblue: "4169e1", saddlebrown: "8b4513", salmon: "fa8072", sandybrown: "f4a460", seagreen: "2e8b57", seashell: "fff5ee", sienna: "a0522d", silver: "c0c0c0", skyblue: "87ceeb", slateblue: "6a5acd", slategray: "708090", snow: "fffafa", springgreen: "00ff7f", steelblue: "4682b4", tan: "d2b48c", teal: "008080", thistle: "d8bfd8", tomato: "ff6347", turquoise: "40e0d0", violet: "ee82ee", violetred: "d02090", wheat: "f5deb3", white: "ffffff", whitesmoke: "f5f5f5", yellow: "ffff00", yellowgreen: "9acd32"}, n = [
            {re: /^rgb\((\d{1,3}),\s*(\d{1,3}),\s*(\d{1,3})\)$/, example: ["rgb(123, 234, 45)", "rgb(255,234,245)"], process: function (e) {
                return[parseInt(e[1], 10), parseInt(e[2], 10), parseInt(e[3], 10), 1]
            }},
            {re: /^rgba\((\d{1,3}),\s*(\d{1,3}),\s*(\d{1,3}),\s*(\d+(?:\.\d+)?|\.\d+)\s*\)/, example: ["rgb(123, 234, 45, 1)", "rgb(255,234,245, 0.5)"], process: function (e) {
                return[parseInt(e[1], 10), parseInt(e[2], 10), parseInt(e[3], 10), parseFloat(e[4])]
            }},
            {re: /^(\w{2})(\w{2})(\w{2})$/, example: ["#00ff00", "336699"], process: function (e) {
                return[parseInt(e[1], 16), parseInt(e[2], 16), parseInt(e[3], 16), 1]
            }},
            {re: /^(\w{1})(\w{1})(\w{1})$/, example: ["#fb0", "f0f"], process: function (e) {
                return[parseInt(e[1] + e[1], 16), parseInt(e[2] + e[2], 16), parseInt(e[3] + e[3], 16), 1]
            }}
        ], r, i, s, o, u, a, f, l, c, h;
        for (u in t) {
            if (e === u) {
                e = t[u]
            }
        }
        for (a = 0; a < n.length; a++) {
            f = n[a].re;
            l = n[a].process;
            c = f.exec(e);
            if (c) {
                h = l(c);
                r = h[0];
                i = h[1];
                s = h[2];
                o = h[3]
            }
        }
        r = r < 0 || isNaN(r) ? 0 : r > 255 ? 255 : r;
        i = i < 0 || isNaN(i) ? 0 : i > 255 ? 255 : i;
        s = s < 0 || isNaN(s) ? 0 : s > 255 ? 255 : s;
        o = o < 0 || isNaN(o) ? 0 : o > 1 ? 1 : o;
        return{r: r, g: i, b: s, a: o, rgba: "rgba(" + r + ", " + i + ", " + s + ", " + o + ")"}
    }, getTextHeight: function (t) {
        var n = e('<span style="font: ' + t + '">Hg</span>'), r = e('<div style="display: inline-block; width: 1px; height: 0px;"></div>'), i = e("<div></div>"), s = e("body"), o = {};
        i.append(n, r);
        s.append(i);
        try {
            o = {};
            r.css({verticalAlign: "baseline"});
            o.ascent = r.offset().top - n.offset().top;
            r.css({verticalAlign: "bottom"});
            o.height = r.offset().top - n.offset().top;
            o.descent = o.height - o.ascent
        } finally {
            i.remove()
        }
        return o
    }}
})(jQuery);
Array.prototype.shuffle = function () {
    "use strict";
    for (var e, t, n = this.length; n; e = parseInt(Math.random() * n, 10), t = this[--n], this[n] = this[e], this[e] = t);
    return this
};
if (!window.setImmediate) {
    window.setImmediate = function () {
        "use strict";
        return window.msSetImmediate || window.webkitSetImmediate || window.mozSetImmediate || window.oSetImmediate || function () {
            if (window.postMessage && window.addEventListener) {
                var e = [], t = -1, n = -1, r = "zero-timeout-message", i = function (t) {
                    e.push(t);
                    window.postMessage(r, "*");
                    return++n
                }, s = function (n) {
                    if (n.data === r) {
                        n.stopPropagation();
                        if (e.length > 0) {
                            var i = e.shift();
                            i();
                            t += 1
                        }
                    }
                }, o;
                window.addEventListener("message", s, true);
                window.clearImmediate = function (r) {
                    if (typeof r !== "number" || r > n) {
                        return
                    }
                    o = r - t - 1;
                    e[o] = function () {
                    }
                };
                return i
            }
        }() || function (e) {
            window.setTimeout(e, 0)
        }
    }()
}
if (!window.clearImmediate) {
    window.clearImmediate = function () {
        "use strict";
        return window.msClearImmediate || window.webkitClearImmediate || window.mozClearImmediate || window.oClearImmediate || function (e) {
            window.clearTimeout(e)
        }
    }()
}
