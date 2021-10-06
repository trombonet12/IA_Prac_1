package agents;

// Exemple de Bitxo
public class Bitxo1 extends Agent {

    static final int PARET = 0;
    static final int BITXO = 1;
    static final int RES = -1;

    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;
    final double dist = 80;
    int repetir;
    int repetirC;

    Estat estat;

    public Bitxo1(Agents pare) {
        super(pare, "Speedy", "imatges/robotank1.gif");
    }

    @Override
    public void inicia() {
        // atributsAgents(v,w,dv,av,ll,es,hy)
        int cost = atributsAgent(6, 5, 600, 30, 23, 5, 5);
        System.out.println("Cost total:" + cost);

        // Inicialització de variables que utilitzaré al meu comportament
        repetir = 0;
        repetirC = 0;
    }

    private void camina() {
        if (estat.enCollisio == true) {
            enrere();
            repetirC = 5;
        } else if ((estat.distanciaVisors[ESQUERRA] < dist) && (estat.objecteVisor[ESQUERRA] == PARET)) {
            dreta();
            System.out.println("dreta");
            repetir = 2;
        } else if ((estat.distanciaVisors[DRETA] < dist) && (estat.objecteVisor[DRETA] == PARET)) {
            esquerra();
            System.out.println("esquerra");
            repetir = 2;
        }
        System.out.println(repetir);
        if (repetir == 0) {
            atura();
            endavant();
            System.out.println("endevant");
        } else {
            repetir--;
        }
    }

    private void cerca() {
        int min = Integer.MAX_VALUE;
        int proper = -1;
        for (int i = 0; i < estat.numObjectes; i++) {
            if ((estat.objectes[i].agafaDistancia() < min)) {
                //if ((estat.objectes[i].agafaTipus() == estat.id + 100) && (estat.objectes[i].agafaDistancia() < min)) {
                min = estat.objectes[i].agafaDistancia();
                proper = i;
            }
        }
        System.out.println(repetirC);
        if (proper != -1) {
            if (repetirC == 0) {
                mira(estat.objectes[proper]);
                System.out.println("Found it");
            } else {
                repetirC--;
            }
        }

    }

    @Override
    public void avaluaComportament() {
        estat = estatCombat();
        cerca();
        camina();
    }

}
