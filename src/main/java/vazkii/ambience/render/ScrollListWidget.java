package vazkii.ambience.render;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.ambience.Screens.SpeakerContainer;
import vazkii.ambience.Screens.SpeakerScreen;
import vazkii.ambience.Util.Handlers.SoundHandler;

@OnlyIn(Dist.CLIENT)
public class ScrollListWidget extends ExtendedList<ScrollListWidget.SoundEntry> {
	public ScrollListWidget(Minecraft mcIn, int width, int height, SpeakerContainer screenContainer,
			FontRenderer font) {
		super(mcIn, width - 52, 120, height / 2 - 71, height / 2 - 4, 15);

		setLeftPos(23);

		for (String sound : SoundHandler.SOUNDS) {
			ScrollListWidget.SoundEntry soundEntry = new SoundEntry(sound, font);
			this.addEntry(soundEntry);

			if (sound.contains(SpeakerContainer.selectedSound)) {
				this.setSelected(soundEntry);
			}
		}

		if (this.getSelected() != null) {
			this.centerScrollOn(this.getSelected());
		}
	}

	public void setSize(int width, int height) {
		updateSize(width - 50, 120, height / 2 - 71, height / 2 - 4);
	}

	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 17;
	}

	public int getRowWidth() {
		return super.getRowWidth() + 15;
	}

	public void setSelected(@Nullable ScrollListWidget.SoundEntry p_setSelected_1_) {		
		super.setSelected(p_setSelected_1_);
	}
	
	public void setSelected(String sound, FontRenderer font) {		
		ScrollListWidget.SoundEntry p_setSelected_1_= new SoundEntry(sound, font);
		super.setSelected(p_setSelected_1_);
	}

	@Override
	protected void renderList(int p_renderList_1_, int p_renderList_2_, int p_renderList_3_, int p_renderList_4_,
			float p_renderList_5_) {

		super.renderList(p_renderList_1_, p_renderList_2_, p_renderList_3_, p_renderList_4_, p_renderList_5_);
	}

	@Override
	protected void renderHoleBackground(int p_renderHoleBackground_1_, int p_renderHoleBackground_2_,
			int p_renderHoleBackground_3_, int p_renderHoleBackground_4_) {

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		this.minecraft.getTextureManager().bindTexture(SpeakerScreen.textureBackground);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos((double) this.x0 + 100, (double) p_renderHoleBackground_2_, 0.0D)
				.tex(0.0F, (float) p_renderHoleBackground_2_ / 153.0F).color(255, 255, 255, p_renderHoleBackground_4_)
				.endVertex();
		bufferbuilder.pos((double) (this.x0 + this.width) - 126, (double) p_renderHoleBackground_2_, 0.0D)
				.tex((float) this.width / 600.0F, (float) p_renderHoleBackground_2_ / 153.0F)
				.color(255, 255, 255, p_renderHoleBackground_4_).endVertex();
		bufferbuilder.pos((double) (this.x0 + this.width) - 126, (double) p_renderHoleBackground_1_, 0.0D)
				.tex((float) this.width / 600.0F, (float) p_renderHoleBackground_1_ / 153.0F)
				.color(255, 255, 255, p_renderHoleBackground_3_).endVertex();
		bufferbuilder.pos((double) this.x0 + 100, (double) p_renderHoleBackground_1_, 0.0D)
				.tex(0.0F, (float) p_renderHoleBackground_1_ / 153.0F).color(255, 255, 255, p_renderHoleBackground_3_)
				.endVertex();
		tessellator.draw();
	}

	@Override
	protected void renderDecorations(int p_renderDecorations_1_, int p_renderDecorations_2_) {
		// TODO Auto-generated method stub
		super.renderDecorations(p_renderDecorations_1_, p_renderDecorations_2_);
	}

	private int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
	}

	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground();
		int i = this.getScrollbarPosition();
		int j = i + 6;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		this.minecraft.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		int k = 5;
		int l = this.y0 + 4 - (int) this.getScrollAmount();
		if (this.renderHeader) {
			this.renderHeader(k, l, tessellator);
		}

		this.renderList(k, l, p_render_1_, p_render_2_, p_render_3_);
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO,
				GlStateManager.DestFactor.ONE);
		RenderSystem.disableAlphaTest();
		RenderSystem.shadeModel(7425);
		RenderSystem.disableTexture();
		int i1 = 4;

		int j1 = this.getMaxScroll();
		if (j1 > 0) {
			int k1 = (int) ((float) ((this.y1 - this.y0) * (this.y1 - this.y0)) / (float) this.getMaxPosition());
			k1 = MathHelper.clamp(k1, 32, this.y1 - this.y0 - 8);
			int l1 = (int) this.getScrollAmount() * (this.y1 - this.y0 - k1) / j1 + this.y0;
			if (l1 < this.y0) {
				l1 = this.y0;
			}

			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferbuilder.pos((double) i, (double) this.y1, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos((double) j, (double) this.y1, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos((double) j, (double) this.y0, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos((double) i, (double) this.y0, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
			tessellator.draw();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferbuilder.pos((double) i, (double) (l1 + k1), 0.0D).tex(0.0F, 1.0F).color(128, 128, 128, 255)
					.endVertex();
			bufferbuilder.pos((double) j, (double) (l1 + k1), 0.0D).tex(1.0F, 1.0F).color(128, 128, 128, 255)
					.endVertex();
			bufferbuilder.pos((double) j, (double) l1, 0.0D).tex(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
			bufferbuilder.pos((double) i, (double) l1, 0.0D).tex(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
			tessellator.draw();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferbuilder.pos((double) i, (double) (l1 + k1 - 1), 0.0D).tex(0.0F, 1.0F).color(192, 192, 192, 255)
					.endVertex();
			bufferbuilder.pos((double) (j - 1), (double) (l1 + k1 - 1), 0.0D).tex(1.0F, 1.0F).color(192, 192, 192, 255)
					.endVertex();
			bufferbuilder.pos((double) (j - 1), (double) l1, 0.0D).tex(1.0F, 0.0F).color(192, 192, 192, 255)
					.endVertex();
			bufferbuilder.pos((double) i, (double) l1, 0.0D).tex(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
			tessellator.draw();
		}

		this.renderDecorations(p_render_1_, p_render_2_);
		RenderSystem.enableTexture();
		RenderSystem.shadeModel(7424);
		RenderSystem.enableAlphaTest();
		RenderSystem.disableBlend();
	}

	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		// TODO Auto-generated method stub
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}

	@Override
	public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_,
			double p_mouseDragged_6_, double p_mouseDragged_8_) {
		// TODO Auto-generated method stub
		return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_,
				p_mouseDragged_8_);
	}

	@Override
	public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
		// TODO Auto-generated method stub
		return super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
	}

	protected void renderBackground() {
		// this.renderBackground();
	}

	protected boolean isFocused() {
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	public class SoundEntry extends ExtendedList.AbstractListEntry<ScrollListWidget.SoundEntry> {
		private final String sound;
		private final FontRenderer font;

		public SoundEntry(String sound, FontRenderer font) {
			this.sound = sound;
			this.font = font;
		}

		public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_,
				int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
			this.font.setBidiFlag(true);
			ScrollListWidget.this.drawCenteredString(this.font, this.sound, ScrollListWidget.this.width / 2 + 20,
					p_render_2_ + 1, 16777215);

			this.font.setBidiFlag(Minecraft.getInstance().getLanguageManager().getCurrentLanguage().isBidirectional());
		}

		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			if (p_mouseClicked_5_ == 0) {
				this.setSelected();
				return true;
			} else {
				return false;
			}
		}

		private void setSelected() {
			ScrollListWidget.this.setSelected(this);
		}

		public String getText() {
			return sound;
		}
	}

}
