DROP TABLE IF EXISTS filters_localization;

CREATE TABLE filters_localization (
filter_list_id INTEGER,
language_code VARCHAR(2),
filter_name VARCHAR(255),
filter_description VARCHAR(1024)
);

INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (1,'pt','Filtro russo','Filtro que permite o bloqueio de anúncios em sites em russo.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (1,'ja','ロシア語フィルター。','ロシアのウェブサイト上の広告をブロックするフィルター。');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (1,'tr','Rusça Filtre','Rusça web sitelerinde reklamların engellenmesini sağlayan filtre');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (1,'en','Russian filter','Filter that enables blocking of ads on websites in Russian.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (1,'es','Filtro para ruso','Filtro que permite el bloqueo de anuncios de sitios web en ruso.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (1,'de','Russischer Filter','Filter, der Anzeigen auf russischen Webseiten blockiert.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (1,'it','Filtro russo','Filtro che abilita il blocco degli annunci sui siti internet in russo.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (1,'sr','Ruski filter','Filter koji blokira reklame na ruskom.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (1,'ru','Русский фильтр','Фильтр, позволяющий убрать рекламу с сайтов на русском языке.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (1,'pl','Rosyjski filtr','Filtr który umożliwia blokowanie reklam na rosyjskich stronach internetowych.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (1,'uk','Російський фільтр','Фільтр, який дозволяє заблокувати рекламу на російськомовних сайтах.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (2,'uk','Англійський фільтр','Фільтр, який дозволяє заблокувати рекламу на англомовних сайтах.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (2,'it','Filtro inglese','Filtro che abilita il blocco degli annunci sui siti internet con contenuto in inglese.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (2,'sr','engleski filter','Filter koji blokira reklame na engleskim sajtovima.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (2,'pl','Angielski filtr','Filtr który umożliwia blokowanie reklam na stronach internetowych zawierających treść po angielsku.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (2,'en','English filter','Based on EasyList. Filter that enables removing of the ads from websites with English content.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (2,'tr','İngilizce Filtre','Tüm sitelerde ve özellikle İngilizce web sitelerinde reklamların engellenmesini sağlayan filtre.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (2,'ja','英語フィルター。','英語コンテンツがあるウェブサイト上の広告をブロックするフィルター。');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (2,'pt','Filtro inglês','Filtro que permite o bloqueio de anúncios em sites com conteúdo em Inglês.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (2,'de','Englischer Filter','Filter, der Anzeigen auf englischen Webseiten blockiert.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (2,'ru','Английский фильтр','Основан на EasyList. Фильтр, позволяющий убрать рекламу с сайтов на английском языке.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (2,'es','Filtro para inglés','Filtro que permite el bloqueo de anuncios de sitios con contenido en inglés.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (3,'sr','Špijunski filter','Najpotpunija lista mrežnih brojača i sistema za praćenje. AKo ne želite da budete praćeni na mreži, koristite ovaj filter.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (3,'en','Spyware filter','The most comprehensive list of various online counters and web analytics tools. If you do not want your actions on the Internet to be tracked, use this filter.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (3,'de','Spyware-Filter','Die umfangreichste Liste von verschiedenen Online-Zähler und Web-Analytics-Tools. Wenn Sie nicht möchten, dass Ihre Aktionen im Internet verfolgt werden, verwenden Sie diesen Filter.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (3,'ru','Фильтр счетчиков и системы аналитики','Наиболее полный список различных интернет-счетчиков и систем интернет-аналитики. Если вы не желаете, чтобы за вашими действиями в интернете следили, используйте этот фильтр.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (3,'pl','Filtr elementów szpiegujących','Najbardziej wszechstronna lista różnych liczników internetowych i narzędzi analizy sieci. Jeśli nie chcesz, aby Twoje działania w Internecie były śledzone, użyj tego filtra.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (3,'pt','Filtro de spyware','A lista mais abrangente de vários contadores online e ferramentas de web analytics. Se você não quer suas ações na internet rastreadas, use este filtro.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (3,'uk','Фільтр лічильників і системи аналітики','Найповніший список різних інтернет-лічильників і систем інтернет-аналітики. Використовуйте цей фільтр, якщо не бажаете, щоб за вашими діями стежили.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (3,'tr','Casus Engelleme Filtresi','Çeşitli online sayaçların ve web analitik araçlarının en kapsamlı listesi. İnternet üzerindeki hareketlerinizin izlenmesini istemiyorsanız bu filtreyi kullanın.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (3,'es','Filtro de spyware','La lista más comprensiva de varios contadores online y herramientas de analítica de webs. Si no quieres que se sigan tus movimientos en Internet, usa este filtro.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (3,'it','Filtro spyware','La più esauriente lista dei vari contatori online e sistemi di tracciatura web. Se non vuoi che le tue azioni in Internet vengano tracciate, usa questo filtro.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (3,'ja','スパイウェアフィルター。','最も幅広い様々なオンラインカウンターのリストとウェブ分析ツール。インターネット上の操作を追跡されたくない場合、このフィルターを使います。');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (4,'ja','ソーシャルメディアフィルター。','ソーシャルメディアウィジェット（“いいね！”ボタンなど）用のフィルター。');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (4,'pt','Filtro de mídia social','Filtro para widgets de mídia social (como botões Like ).');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (4,'pl','Filtr mediów społecznościowych','Filtr widżetów mediów społecznościowych (przyciski polubień i podobne).');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (4,'uk','Фільтр віджетів соціальних мереж','Фільтр віджетів соціальних мереж (кнопки ""Мені подобається"" тощо).');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (4,'en','Social media filter','Filter for social media widgets (Like buttons and such).');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (4,'tr','Sosyal Ağ araçları filtresi','Sosyal ağ araçları (""Beğen"" butonları vb) için bir filtre.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (4,'es','Filtro de redes sociales','Filtro para widgets de redes sociales (botones Like y demás).');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (4,'sr','Filter društvenih mreža','Filter za vidžete društvenih mreža kao što su lajk dugmići i slično.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (4,'it','Filtro per i widget dei social network','Filtro per i widget dei social network (bottoni ""mi piace"" e così via).');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (4,'ru','Фильтр виджетов социальных сетей','Фильтр для виджетов социальных сетей (кнопки Мне нравится и тому подобное).');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (4,'de','Social-Media-Filter','Filter für Widgets der Sozialnetzwerken (Like-Buttons und andere).');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (5,'pt','Filtro experimental','Filtro concebido para testar certas regras de filtragem perigosas antes de serem adicionadas aos filtros básicos.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (5,'it','Filtro sperimentale','Filtro studiato per testare alcune pericolose regole di filtraggio prima che vengano aggiunte ai filtri base.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (5,'sr','Eksperimentalni filter','Filter napravljen za testiranja nekih pravila, pre nego se ista dodaju u službene filtere.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (5,'pl','Eksperymentalny filtr','Filtr przeznaczony do testowania niektórych ryzykownych reguł filtrowania zanim zostaną dodane do podstawowych filtrów.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (5,'ja','エクスペリメンタルフィルター。','害を与える可能性があるフィルタリングルールが、基本的なフィルターへ追加される前にテストを行うフィルター。');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (5,'es','Filtro experimental','Filtro diseñado para probar ciertas reglas de filtrado peligrosas antes de que se añadan a los filtros básicos.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (5,'tr','Deneysel filtre','Temel filtrelere eklenmeden önce bazı tehlikeli filtreleme kurallarının test edilmesi için tasarlanmıştır.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (5,'en','Experimental filter','Filter designed to test certain hazardous filtering rules before they are added to the basic filters.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (5,'de','Experimenteller Filter','Filter, der zum Testen von bestimmten gefährlichen Filterungsregeln dient, bevor sie dem Basisfilter hinzugefügt werden.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (5,'uk','Експериментальний фільтр','Фільтр, призначений для перевірки деяких правил фільтрації перш ніж як вони будуть додані в основні фільтри.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (5,'ru','Экспериментальный фильтр','Фильтр, предназначенный для проверки некоторых опасных правил фильтрации перед тем как они будут добавлены в основные фильтры.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (6,'pl','Niemiecki filtr','Filtr który umożliwia usuwanie reklam ze stron internetowych zawierających treść po niemiecku.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (6,'en','German filter','Filter that enables removing of the ads from websites with German content.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (6,'it','Filtro tedesco','Filtro che abilita il blocco degli annunci sui siti internet con contenuto in tedesco.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (6,'de','Deutscher Filter','Filter, der Anzeigen auf deutschen Webseiten blockiert.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (6,'ru','Немецкий фильтр','Фильтр, позволяющий убрать рекламу с сайтов на немецком языке.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (6,'uk','Німецький фільтр','Фільтр, який дозволяє заблокувати рекламу на німецькомовних сайтах.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (6,'ja','ドイツ語フィルター。','ドイツ語コンテンツがあるウェブサイト上の広告をブロックするフィルター。');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (6,'pt','Filtro alemão','Filtro que permite o bloqueio de anúncios em sites com conteúdo alemão.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (6,'es','Filtro para alemán','Filtro que permite el bloqueo de anuncios de sitios con contenido en alemán.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (6,'sr','Nemački filter','Filter koji uklanja reklame sa sajtova nemačkog govornog područja.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (6,'tr','Almanca filtre','Almanca web sitelerinde reklamların engellenmesini sağlayan filtre.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (7,'es','Filtro para japonés','Filtro que permite el bloqueo de anuncios de sitios web en japonés.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (7,'de','Japanischer Filter','Filter, der Anzeigen auf japanischen Webseiten blockiert.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (7,'en','Japanese filter','Filter that enables blocking of ads on websites in Japanese.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (7,'it','Filtro giapponese','Filtro che abilita il blocco degli annunci sui siti internet in giapponese.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (7,'ja','日本語フィルター。','日本語のウェブサイト上の広告をブロックするフィルター。');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (7,'pl','Japoński filtr','Filtr który umożliwia blokowanie reklam na japońskich stronach internetowych.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (7,'pt','Filtro japonês','Filtro que permite o bloqueio de anúncios em sites em japonês.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (7,'ru','Японский фильтр','Фильтр, позволяющий убрать рекламу с сайтов на японском языке.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (7,'sr','japanski filter','Filter koji blokira reklame na japanskim sajtovima.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (7,'tr','Japonca filtre','Japonca web sitelerinde reklamların engellenmesini sağlayan filtre.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (7,'uk','Японський фільтр','Фільтр, який дозволяє заблокувати рекламу на японськомовних сайтах.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (8,'ja','オランダ語フィルター。','オランダ語コンテンツがあるウェブサイト上の広告をブロックするフィルター。');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (8,'de','Niederländischer Filter','Filter, der Anzeigen auf niederländischen Webseiten blockiert.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (8,'tr','Hollandaca filtre','Hollandaca web sitelerinde reklamların engellenmesini sağlayan filtre.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (8,'es','Filtro para holandés','Filtro que permite el bloqueo de anuncios de sitios con contenido en holandés.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (8,'it','Filtro olandese','Filtro che abilita il blocco degli annunci sui siti internet con contenuto in olandese.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (8,'sr','Holandski filter','Filter koji blokira reklame na holandskim sajtovima.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (8,'pt','Filtro holandês','Filtro que permite o bloqueio de anúncios em sites com conteúdo holandês.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (8,'en','Dutch filter','Filter that enables blocking of ads on websites with Dutch content.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (8,'ru','Голландский фильтр','Фильтр, позволяющий убрать рекламу с сайтов на голландском языке.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (8,'pl','Holenderski filtr','Filtr który umożliwia blokowanie reklam na stronach internetowych zawierających treść po holendersku.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (8,'uk','Голландський фільтр','Фільтр, який дозволяє заблокувати рекламу на голландських сайтах.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (9,'tr','İspanyolca/Portekizce filtre','İspanyolca ve Portekizce web sitelerinde reklamların engellenmesini sağlayan filtre.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (9,'pt','Filtro espanhol/português','Filtro que permite o bloqueio de anúncios em sites em espanhol e português.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (9,'de','Spanischer/Portugiesischer Filter','Filter, der Anzeigen auf spanischen/portugiesischen Webseiten blockiert.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (9,'pl','Hiszpański/Portugalski filtr','Filtr który umożliwia blokowanie reklam na hiszpańskich i portugalskich stronach internetowych.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (9,'ru','Испано-португальский фильтр','Фильтр, позволяющий убрать рекламу с сайтов на испанском и португальском языках.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (9,'ja','スペイン語/ポルトガル語フィルター。','スペイン語とポルトガル語のウェブサイト上にある広告をブロックするフィルター。');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (9,'uk','Іспано-португальський фільтр','Фільтр, який дозволяє заблокувати рекламу на сайтах з іспанською та португальською мовами.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (9,'sr','Španski i portugalski filter','Filter koji blokira reklame na španskom i portugalskom.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (9,'it','Filtro spagnolo/portoghese','Filtro che abilita il blocco degli annunci sui siti internet in spagnolo e portoghese.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (9,'es','Filtro para español/portugués','Filtro que permite el bloqueo de anuncios de sitios web en español y portugués.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (9,'en','Spanish/Portuguese filter','Filter that enables blocking of ads on websites in Spanish and Portuguese.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (10,'sr','Filter za korisne reklame','Filter koji deblokira reklame, koje mogu biti zanimljive korisniku, kao što su reklame zasnovane na pretraživanju.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (10,'pt','Filtro para anúncios úteis','Filtro que desbloqueia anúncios, que podem ser úteis para os usuários.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (10,'ru','Фильтр полезной рекламы','Фильтр, разблокирующий рекламу, которая может быть полезна пользователям.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (10,'es','Filtro para anuncios útiles','Filtro que desbloquea anuncios, que podría ser útil para los usuarios.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (10,'tr','Yararlı reklamlar için filtre','Kullanıcı için yararlı olabilecek reklamların listesi.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (10,'en','Filter for useful ads','Filter that unblocks ads, which may be useful to users.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (10,'ja','有益広告フィルター。','ユーザーにとって有益な広告をブロック解除するフィルター。');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (10,'de','Filter für nützliche Werbung','Filter, der Anzeigen zulässt, die für den Benutzer hilfreich sein können.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (10,'uk','Фільтр корисної реклами','Фільтр, що розблоковує рекламу, яка може бути корисна користувачам.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (10,'it','Filtro per annunci utili','Filtro che sblocca gli annunci, i quali potrebbero essere utili agli utenti.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (10,'pl','Filtr użytecznych reklam','Filtr służący do odblokowania reklam, które mogą być przydatne użytkownikom.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (11,'es','Filtro de publicidad móvil','Filtro para todas las redes de anuncios de móvil conocidas');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (11,'ja','モバイル広告フィルター','既知のモバイル広告ネットワーク全てを対象としたフィルター');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (11,'ru','Фильтр мобильной рекламы','Фильтр для мобильных рекламных сетей');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (11,'pl','Filtr reklam dla urządzeń mobilnych','Filtr dla wszystkich znanych mobilnych sieci reklamowych.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (11,'en','Mobile ads filter','Filter for all known mobile ad networks');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (11,'pt','Filtro de anúncios em celular','FIltrar todas as redes de publicidade móvel conhecidas ');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (11,'uk','Фільтр мобільної реклами','Фільтр мобільних рекламних мереж');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (11,'sr','Filter mobilnih reklama','Filter za sve poznate mreže mobilnih reklama');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (11,'it','Filtro per annunci sui cellulari','Filtro per tutti gli annunci per cellulari conosciuti');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (11,'tr','Mobil reklam filtresi','Bilinen tüm mobil reklam ağları için filtre');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (11,'de','Filter für Handywerbung','Filter für alle üblichen Handy-Werbenetzwerke');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (13,'en','Turkish filter','Filter that enables blocking of ads on websites in Turkish.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (13,'tr','Türkçe Filtre','Türkçe web sitelerinde reklamların engellenmesini sağlayan filtre.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (13,'it','Filtro Turco','Filtro che abilita il blocco degli annunci sui siti internet in turco.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (13,'sr','Turski filter','Filter koji blokira reklame na turskom.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (13,'uk','Турецький фільтр','Дозволяє прибрати рекламу з сайтів з турецькою мовою.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (13,'pl','Turecki filtr','Filtr który umożliwia blokowanie reklam na tureckich stron internetowych.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (13,'ru','Турецкий фильтр','Фильтр, позволяющий убрать рекламу с сайтов на турецком языке.');
INSERT INTO filters_localization (filter_list_id, language_code, filter_name, filter_description) VALUES (13,'de','Türkischer Filter','Filter, der Anzeigen auf türkischen Webseiten blockiert.');

CREATE TABLE enabled_filters AS
SELECT filter_list_id FROM filter_lists WHERE enabled = 1;

DROP TABLE filter_lists;
CREATE TABLE filter_lists (
filter_list_id INTEGER,
filter_name VARCHAR(255),
filter_description VARCHAR(1024),
enabled INTEGER,
version VARCHAR(255),
time_last_downloaded BIGINT,
time_updated BIGINT,
display_order INTEGER
);

INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (2,'English filter','Based on EasyList. Filter that enables removing of the ads from websites with English content.',0,'1.0.72.58',1449657455523,1449657455523,1);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (3,'Spyware filter','The most comprehensive list of various online counters and web analytics tools. If you do not want your actions on the Internet be tracked, use this filter.',0,'1.0.4.7',1449592122097,1449592122097,2);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (4,'Social media filter','A filter for social media widgets (""Like"" buttons and such).',0,'1.0.7.63',1449592129212,1449592129212,3);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (5,'Experimental filter','Filter designed to test certain hazardous filtering rules before they are added to the basic filters.',0,'1.0.23.36',1449655227432,1449655227432,4);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (10,'Filter for useful ads','Filter that unblocks ads that may be useful to users.',0,'1.0.0.84',1449512148677,1449512148677,5);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (1,'Russian filter','Filter that enables removing of the ads from websites in Russian.',0,'1.0.29.16',1449657435821,1449657435821,6);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (6,'German filter','Filter designed to test certain hazardous filtering rules before they are added to the basic filters.',0,'1.0.22.17',1449592138617,1449592138617,7);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (7,'Japanese filter','Filter that enables removing of the ads from websites in Japanese.',0,'1.0.92.89',1449243032966,1449243032966,8);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (8,'Dutch filter','Filter that enables removing of the ads from websites with Dutch content.',0,'1.0.8.60',1449243040505,1449243040505,9);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (9,'Spanish/Portuguese filter','Filter that enables removing of the ads from websites in Spanish and Portuguese.',0,'1.0.86.12',1449246640691,1449246640691,10);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (13,'Turkish filter','Filter that enables removing of the ads from websites in Turkish.',0,'1.0.0.56',1449579865862,1449579865862,11);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (11,'Mobile ads filter','Filter for all known mobile ad networks',0,'1.0.1.71',1449579851159,1449579851159,99);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (102,'ABPindo','Bahasa Indonesia supplement for EasyList.',0,'1.0.0.26',1448517648570,1448517648570,102);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (104,'EasyList China','中文 supplement for EasyList',0,'1.0.49',1449655270251,1449655270251,104);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (105,'EasyList Czech and Slovak','čeština, slovenčina supplement for EasyList',0,'1.0.0.46',1447178479686,1447178479686,105);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (108,'EasyList Hebrew','Supplement for EasyList. Language: עברית',0,'1.0.1.91',1449612686520,1449612686520,108);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (109,'EasyList Italy','italiano supplement for EasyList',0,'1.0.48.72',1449655309429,1449655309429,109);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (112,'Liste AR','Supplement for EasyList. Language: العربية',0,'1.0.0.87',1440554555966,1440554555966,112);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (113,'Liste FR','français supplement for EasyList',0,'1.0.48.87',1449655339486,1449655339486,113);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (200,'ABP Japanese Filters','日本語',0,'1.0.2.30',1449598000476,1449598000476,200);
INSERT INTO filter_lists (filter_list_id, filter_name, filter_description, enabled, version, time_last_downloaded, time_updated, display_order) VALUES (216,'Adblock polskie reguły','polski filtr',0,'1.0.2.83',1449641149134,1449641149134,216);

UPDATE filter_lists
   SET enabled = 1
 WHERE filter_list_id IN (SELECT filter_list_id FROM enabled_filters);

DROP TABLE enabled_filters;