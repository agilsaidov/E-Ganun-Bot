package com.project.e_ganun.bot;

import com.project.e_ganun.config.BotConfig;
import com.project.e_ganun.model.CodeType;
import com.project.e_ganun.model.Law;
import com.project.e_ganun.model.LawId;
import com.project.e_ganun.model.Usage;
import com.project.e_ganun.service.BotUserService;
import com.project.e_ganun.service.LawService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GanunBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final LawService ganunService;
    private final BotUserService botUserService;

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            User user = update.getMessage().getFrom();

            String codeText = "/cm -> Cinayət Məcəlləsi\n" +
                              "/ixm -> İnzibati Xətalar Məcəlləsi";

            switch (messageText) {
                case "/start":
                    sendWelcomeMessage(chatId);
                    botUserService.registerOrUpdateUser(user);
                    break;
                case "/haqqinda":
                    sendAboutMessage(chatId);
                    break;
                case "/stats":
                    sendUserStats(chatId, user.getId());
                    break;

                case "/mecelle", "/məcəllə":
                    CodeType codeType = botUserService.getCodeType(user.getId());
                    if(codeType != null) {
                        sendMessage(chatId, "Aktiv Məcəllə: " + codeType.getDisplayName());
                        return;
                    }
                    sendMessage(chatId, "\uFE0F Sizin seçilmiş məcəlləniz yoxdur\n" +
                            "\uFE0F Məcəllə seçmək üçün\n" + codeText + "\n əmirlirindən birini cağırın");
                    break;

                case "/cm","/ixm":
                    Usage usage = botUserService.changeCode(user.getId(), messageText);
                    sendMessage(chatId,"\uFE0F Aktiv məcəllə dəyişdi\n" +
                            "\uD83D\uDFE2 Yeni Məcəllə: " + usage.getLastSearchCode().getDisplayName());
                    break;

                default:
                    Usage botUsage = botUserService.trackSearch(user.getId(), messageText);
                    if (botUsage.getLastSearchCode() == null) {
                        sendMessage(chatId, "⚠️ Zəhmət olmasa əvvəl məcəllə seçin:\n" + codeText);
                        return;
                    }
                    LawId lawId = new LawId(messageText, botUsage.getLastSearchCode());
                    searchGanun(chatId, lawId);
                    break;
            }
        }

    }

    @Override
    public String getBotUsername() {
        return botConfig.getToken();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    //Helper Methods

    private void sendWelcomeMessage(Long chatId) {
        String welcome = "🇦🇿 E-Ganun botuna xoş gəlmisiniz!\n" +
                "\uD83D\uDCDC Maddələr üçün nömrə daxil edin (məs: 241)\n" +
                "📊 Statistikanız üçün /stats\n" +
                "❓ Bot haqqında məlumat /haqqinda";
        sendMessage(chatId, welcome);
    }

    private void sendUserStats(Long chatId, Long telegramId) {
        var usage = botUserService.getUserUsage(telegramId);

        if (usage == null) {
            sendMessage(chatId, "❗Statistika tapılmadı");
            return;
        }

        String stats = "📊 Sizin statistikanız:\n\n" +
                "🔍 Axtarışlar: " + usage.getTotalSearches() + "\n" +
                "💬 Mesajlar: " + usage.getTotalMessages() + "\n" +
                "📅 İlk istifadə: " + formatDate(usage.getFirstUsageDate()) + "\n" +
                "🕐 Son axtarış: " + formatDate(usage.getLastSearchDate());

        sendMessage(chatId, stats);
    }

    private void sendAboutMessage(Long chatId) {
        String about =
                "ℹ️ *E-Ganun Botu*\n\n" +
                        "E-Ganun botu Azərbaycan Respublikası qanunvericiliyinə dair məlumatları rəsmi mənbələr əsasında təqdim etmək məqsədilə hazırlanmış köməkçi botdur.\n\n" +
                        "❗ *Qeyd:*\n" +
                        "Bot rəsmi hüquqi mənbə hesab edilmir. Məlumatlar əsasən rəsmi mənbələrə söykənsə də, mümkün texniki vəya məzmun xətalarına görə bot və onun yaradıcısı heç bir məsuliyyət daşımır.";
        sendMessage(chatId, about);
    }


    private void searchGanun(Long chatId, LawId lawId) {

        List<Law> results = ganunService.searchByLawId(lawId);

        if (results.isEmpty()) {
            sendMessage(chatId, "❌ Qanun tapılmadı");
            return;
        }

        String response = formatResults(results);
        sendMessage(chatId, response);
    }

    private String formatResults(List<Law> laws) {
        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDCDC Tapılan qanunlar:\n\n");

        for (Law law : laws) {
            sb.append("🔹 Maddə ").append(law.getLawNo()).append("\n\n");
            sb.append(law.getLawText()).append("\n");
            sb.append("─────────────────\n\n");
        }
        return sb.toString();
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        }catch (TelegramApiException e){
            sendMessage(chatId, "❌ Xəta");
            e.printStackTrace();
        }
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "Yoxdur";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTime.format(formatter);
    }
}
