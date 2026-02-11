package com.project.e_ganun.bot;

import com.project.e_ganun.config.BotConfig;
import com.project.e_ganun.model.Law;
import com.project.e_ganun.service.BotUserService;
import com.project.e_ganun.service.GanunService;
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
    private final GanunService ganunService;
    private final BotUserService botUserService;

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            User user = update.getMessage().getFrom();

            if(messageText.equals("/start")){
                sendWelcomeMessage(chatId);
                botUserService.registerOrUpdateUSer(user);
            }
            else if(messageText.equals("/haqqında") || messageText.equals("/haqqinda") || messageText.equals("/about")){
                sendAboutMessage(chatId);
            }
            else if(messageText.equals("/stats")){
                sendUserStats(chatId, user.getId());
            }
            else{
                botUserService.trackSearch(user.getId(), messageText);
                searchGanun(chatId, messageText);
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


    private void searchGanun(Long chatId, String ganunNo) {
        List<Law> results = ganunService.searchByGanunNo(ganunNo);

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
