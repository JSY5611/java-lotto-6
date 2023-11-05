package lotto.controller;

import lotto.model.*;
import lotto.view.InputView;
import lotto.view.OutputView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LottoController {

    private final OutputView outputView;
    private final InputView inputView;
    private final LottoNumbers lottoNumbers;
    private LottoResult lottoResult;
    private HashMap<LottoType, Integer> lottoStatistics;

    private static int bonus;
    private static List<Lotto> playerLottos = new ArrayList<>();
    private static List<Lotto> winningLotto = new ArrayList<>();

    public LottoController() {
        this.outputView = new OutputView();
        this.inputView = new InputView();
        this.lottoNumbers = new LottoNumbers();
        this.lottoStatistics = new HashMap<>();
    }

    public void run() {
        Price price = inputPrice();
        outputView.printLottoTicketMessage(price.getLottoTicket());
        createPlayerLotto(price.getLottoTicket());
        winningLotto = inputWinningLotto();
        bonus = inputBonusNumber();
        outputView.printWinningStatisticsMessage();

        matchLotto(bonus);
        printLottoStatistics();
        outputView.printProfitRateMessage(lottoResult.rateOfReturn(price.getPrice()));

    }

    private Price inputPrice() {
        try {
            return new Price(inputView.InputMoney());

        } catch (IllegalArgumentException e) {
            outputView.printExceptionMessage(e.getMessage());
            return inputPrice();
        }
    }

    private List<Lotto> inputWinningLotto() {
        try {
            return lottoNumbers.validateWinningLotto(inputView.InputLottoNumber());
        } catch (IllegalArgumentException e) {
            outputView.printExceptionMessage(e.getMessage());
            return inputWinningLotto();
        }
    }

    private int inputBonusNumber() {
        try {
            return LottoValidator.validateBonus(winningLotto,
                    Integer.parseInt(inputView.InputBonusNumber()));

        } catch (IllegalArgumentException e) {
            outputView.printExceptionMessage(e.getMessage());
            return inputBonusNumber();
        }
    }

    private void matchLotto(int bonus) {
        this.lottoResult = new LottoResult(playerLottos, winningLotto, bonus);
        this.lottoResult.compareLotto();
    }

    private List<Lotto> createPlayerLotto(int ticket) {
        for (int i = 0; i < ticket; i++) {
            playerLottos.add(createLotto());
        }
        return playerLottos;
    }

    private Lotto createLotto() {
        LottoNumbers lottoNumbers = new LottoNumbers();
        List<Integer> lotto = lottoNumbers.createPlayerLotto();
        outputView.printLottoList(lotto);
        return new Lotto(lotto);
    }

    private void printLottoStatistics() {
        lottoStatistics = lottoResult.getMatchLottoCountMap();
        for(int i = 0; i < LottoType.values().length - 1; i++) {
            LottoType lottoType = LottoType.values()[i];
            outputView.printMatchedCountMessage(lottoType);
            try {
                outputView.printWinningMoneyMessage(lottoType.getPrize(), lottoStatistics.get(lottoType));
            } catch (NullPointerException e) {
                outputView.printWinningMoneyMessage(lottoType.getPrize(), 0);
            }

        }
    }

}