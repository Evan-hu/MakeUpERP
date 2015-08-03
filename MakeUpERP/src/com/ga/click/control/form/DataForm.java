package com.ga.click.control.form;

import java.util.ArrayList;
import java.util.List;
import org.apache.click.Control;
import org.apache.click.control.Field;
import org.apache.click.control.Form;
import org.apache.click.util.ContainerUtils;
import org.apache.click.util.HtmlStringBuffer;
import com.ga.click.control.DbField;
import com.ga.click.control.GaConstant;
import com.ga.click.control.button.ActionButton;
import com.ga.click.exception.BizException;
import com.ga.click.layout.FormLayout;
import com.ga.click.layout.Layout;
import com.ga.click.mvc.UserACL;
import com.ga.click.util.ClickUtil;

public class DataForm extends Form{
  private static final long serialVersionUID = 1L;
  private static final int DEFAULT_PAGESIZE = 20;
  private List<ActionButton> buttonList = new ArrayList<ActionButton>();
  private List<DbField> dbField;
  private boolean hiddenButton = false;
  private UserACL userAcl = null;
  
  private Layout layout = null;
  
  public void setLayout(Layout layout) {
    this.layout = layout;
  }
  
  public Layout getLayout() {
    return this.layout;
  }
  
  public List<DbField> getDbField() {
    return dbField;
  }

public DataForm(String formID,List<DbField> dbField, int editMode,UserACL userAcl) {
    this.setName(formID);
    try {  
      //�����ؼ�
      this.userAcl=userAcl;
      this.dbField = dbField;
      if (dbField != null) {
        for(DbField field :dbField) {
          int inputMode = field.getInputMode(editMode);
          if (inputMode == GaConstant.INPUTMODE_HIDDEN) {
            //���ز��������
            continue;
          }
          //�����ؼ�
          Field control = ClickUtil.createControl(field);
          //����ģʽ��������
          if (inputMode == GaConstant.INPUTMODE_READONLY) {
            if (field.getInputType() == GaConstant.INPUT_DATETIME) {
              control.setDisabled(true);
            } else {
            	if(control == null){
            		System.out.println(1);
            	}
              control.setReadonly(true);
            }
          }
          this.add(control);
        }
      }    
    } catch(Exception ex) {
    	ex.printStackTrace();
      throw new BizException(BizException.SYSTEM,"�������ݱ����쳣;"+ex.getMessage());
    }
  }

  public String getFieldValue(String fieldCode) {
    Field paramField = getField(fieldCode);
    if (paramField != null) {
        return paramField.getValue();
    } else {
      return "";
    }
  }
  
  public void regFormButton(ActionButton button) {
    if (this.userAcl == null || !this.userAcl.checkAction(button.getId(),button.getTitle())) {
      return;
    }
    this.buttonList.add(button);
  }
  

  
  
  public List<ActionButton> getActionButtonList() {
    return this.buttonList;
  }

  private boolean isHidden(Control control) {
    if (!(control instanceof Field)) {
        // Non-Field Controls can not be hidden
        return false;
    } else {
        return ((Field) control).isHidden();
    }
  }


  /**
   * Render the HTML representation of the Form.
   * <p/>
   * If the form contains errors after processing, these errors will be
   * rendered.
   * 
   * @see #toString()
   * 
   * @param buffer
   *            the specified buffer to render the control's output to
   */
  @Override
  public void render(HtmlStringBuffer buffer) {
      
      final boolean process = getContext().getRequest().getMethod().equalsIgnoreCase(getMethod());
  
      List<Field> formFields = ContainerUtils.getInputFields(this);
      if (this.layout == null) {
        //Ĭ�ϳ�ʼ�����ֿؼ�
        this.layout = new FormLayout("",this,1);
      } 
      
      buffer.append("<div ");
      if (this.layout.getLayoutH() == -1) {
        buffer.append(">");
      } else {
        buffer.append(" layoutH=\"").append(this.layout.getLayoutH()).append("\" >\n");
      }
      // ��Ⱦform���
      renderHeader(buffer, formFields);
      // ��Ⱦ����ؼ�
 
      this.layout.renderMain(buffer);
      if (!this.hiddenButton) {
        this.layout.renderButton(buffer);
      }
      // �ر�form�༭
      renderTagEnd(formFields, buffer);
      buffer.append("</div><script>$(document).ready(function() {$(\"#" + getName()
              + "\").validationEngine({showOnMouseOver:true})});</script>\n");
  
  }
  
  /**
   * Render the given form start tag and the form hidden fields to the given
   * buffer.
   * 
   * @param buffer
   *            the HTML string buffer to render to
   * @param formFields
   *            the list of form fields
   */
  protected void renderHeader(HtmlStringBuffer buffer, List<Field> formFields) {
      buffer.elementStart(getTag());
      buffer.appendAttribute("method", getMethod());
      buffer.appendAttribute("name", getName());
      buffer.appendAttribute("id", getId());
      buffer.appendAttribute("action", getActionURL());
      buffer.appendAttribute("enctype", getEnctype());
      buffer.appendAttribute("class", "pageForm required-validate");
            
      buffer.closeTag();
      buffer.append("\n");
  
      //�������ؿؼ�
      for (Control control : controls) {
        if (isHidden(control)) {
          control.render(buffer);
          buffer.append("\n");
        }
      }
  }
  
  public void hiddenFormButton() {
    hiddenButton = true;
  }
  
  public boolean isHiddenFormButton() {
    return this.hiddenButton;
  }
  
  
  @Override
  public String getTag() {
      return "form";
  }
}