Page {
	padding: "8"
	title: "NoCodeBrowser"

	Column {
		padding: "8"
						
		Markdown {
			color: "#4C9BD9"
			text: "# Bonvenon"
  		}
  		Markdown { 
      		fontSize: 16
			text :"
				Ni ĝojas, ke vi uzas la NoCode-solvon.
				Bonvolu noti, ke ĉi tio estas ankoraŭ
				frua versio, kiu eble enhavas
				kelkajn erarojn.
				La enhavo de ĉi tiu apo estis kreita per la
				**NoCodeDesigner**, aplikaĵo por la
				labortablo."
		}
		Spacer { amount: 16 }
		Markdown {
			color: "#4C9BD9"
			text: "### NoCodeDesigner"
 		}
		Spacer { amount: 8 }
		Image { src: "desktop.png" }
		Spacer { amount: 16 }
		Markdown {
			fontSize: 16
			text: "Komencu nun, kreu E-libron aŭ
				eĉ apon, kaj krei enhavon por
				aliaj homoj, kiuj helpos vin
				proksimiĝi al viaj revoj."}

		Spacer { weight: 1 }
		Button { label: "Pri NoCode" link: "page:about"}
	}
}