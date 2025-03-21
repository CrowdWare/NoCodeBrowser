Page {
	padding: "8"
	title: "Pri"

	Column {
		padding: "8"

		Markdown { text: "# Pri ĉi tiu apo" }
		Spacer { amount: 8 }
		Markdown {
			text: "Vi povas desegni tiajn aplikojn per la FreeBookDesigner.
				Por testi tian apon, vi bezonas ĉi tiun retumilan aplikaĵon." 
		}
		Spacer {amount: 8}
		Row {
			Markdown {
				text:"Malfermu la menuon (hamburger-
					simbolo) kaj elektu Agordojn.
					Aldonu ligilon en la agorda
					paĝo.
					Donu unikan titolon al la ligilo,
					kiu aperos poste en la menuo sub
					Agordoj.
					Enmetu nun la URL-on al
					via retservilo."
				}
			Spacer {amount: 8}
			Image { src: "menu.png" scale:"fit"}
		}
		Spacer {amount: 8}
		Markdown { 
			text:"Tiu URL normale aspektas simile al
				**http://192.168.178.21:8000/**, kiu estas la vojo 
				al via loka retservilo. 
				Tiu URL aperos kiam vi startos vian
				Python-bazitan test-retservilon
				en via projektdosierujo per:
				**python server.py** 
				Simple alklaku la butonon [Aldoni ligilon], 
				kaj la ligilo estu konservita.
				Kiam vi remalfermos la menuon, vi devus vidi la novan menueron.
				Alklaku tiun elementon kaj via apo aŭtomate ŝarĝos.

				**La uzantointerfaco de la retumila apo malaperos ĝis vi restartigos la retumilon.**"
		}
    		Spacer { weight: 1 }
   		Button { label: "Reen al la Hejmpaĝo" link: "page:app.home" }
	}
}