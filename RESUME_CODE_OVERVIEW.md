# 简历管理系统 - 代码架构说明

> **文档版本**: v1.0  
> **最后更新**: 2026-01-09  
> **系统概述**: 智能简历分析与管理系统，支持简历上传、解析、分析、版本管理和技能提取

---

## 📋 目录

1. [系统架构概览](#系统架构概览)
2. [数据模型层 (Models)](#数据模型层)
3. [数据访问层 (DAO)](#数据访问层)
4. [数据类型定义 (Types)](#数据类型定义)
5. [业务服务层 (Service)](#业务服务层)
6. [API控制层 (Controller)](#api控制层)
7. [核心功能流程](#核心功能流程)
8. [完整代码清单](#完整代码清单)

---

## 系统架构概览

### 架构分层

```
┌─────────────────────────────────────────────────┐
│              API 控制层 (Controller)              │
│         resume_router.py - RESTful API          │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────┴────────────────────────────────┐
│              业务服务层 (Service)                 │
│  ┌─────────────┬──────────────┬──────────────┐ │
│  │resume_parser│resume_analyzer│skill_extractor│ │
│  └─────────────┴──────────────┴──────────────┘ │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────┴────────────────────────────────┐
│             数据访问层 (DAO)                      │
│  ┌──────────────────┬──────────────────────┐   │
│  │resume_version_dao│ resume_skill_dao     │   │
│  └──────────────────┴──────────────────────┘   │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────┴────────────────────────────────┐
│              数据模型层 (Models)                  │
│  ┌────────┬──────────────┬──────────────┐      │
│  │ Resume │ResumeVersion │ ResumeSkill  │      │
│  └────────┴──────────────┴──────────────┘      │
└─────────────────────────────────────────────────┘
```

### 核心模块职责

| 层级 | 模块 | 文件路径 | 职责描述 |
|------|------|----------|----------|
| **API控制层** | resume_router | `api/app/controller/resume_router.py` | 处理HTTP请求，路由分发，请求验证 |
| **服务层** | resume_parser | `api/app/service/resume_parser.py` | 文件解析，文本提取，LLM结构化解析 |
| **服务层** | resume_analyzer | `api/app/service/resume_analyzer.py` | 简历智能分析，生成Markdown报告 |
| **服务层** | skill_extractor | `api/app/service/skill_extractor.py` | 技能提取与分类 |
| **DAO层** | resume_version_dao | `api/app/dao/resume_version_dao.py` | 版本管理数据库操作 |
| **DAO层** | resume_skill_dao | `api/app/dao/skill_dao.py` | 技能数据库操作 |
| **模型层** | models | `api/app/dao/models/models.py` | 数据库表定义，ORM映射 |
| **类型层** | resume_types | `api/app/types/resume_types.py` | Pydantic数据模型，请求/响应定义 |

---

## 数据模型层

### 1. Resume 表（简历主表）

**表名**: `resumes`

```python
class Resume(Base):
    """简历表 - 存储简历基本信息"""
    
    id = Column(String(36), primary_key=True)           # 简历ID (UUID)
    user_id = Column(String(36), ForeignKey("ym_user.id"))  # 所属用户ID
    name = Column(String(64), nullable=False)           # 候选人姓名
    email = Column(String(128))                         # 邮箱
    phone = Column(String(32))                          # 电话
    raw_text = Column(Text)                             # 原始简历文本
    parsed_data = Column(JSON)                          # 解析后的结构化数据
    file_name = Column(String(256))                     # 原始文件名
    file_size = Column(Integer)                         # 文件大小(字节)
    parse_status = Column(String(32), default="pending") # 解析状态
    parse_error = Column(Text)                          # 解析错误信息
    created_at = Column(DateTime, default=cst_now)      # 创建时间
    updated_at = Column(DateTime, onupdate=cst_now)     # 更新时间
```

**关系定义**:
- 一对多：`resume_versions` - 一个简历可以有多个版本
- 一对多：`resume_skills` - 一个简历可以有多个技能关联
- 多对一：`user` - 每个简历属于一个用户

**索引**:
- `idx_resume_user_id` - 按用户查询
- `idx_resume_email` - 按邮箱查询
- `idx_resume_parse_status` - 按解析状态查询

---

### 2. ResumeVersion 表（简历版本历史）

**表名**: `resume_versions`

```python
class ResumeVersion(Base):
    """简历版本历史表 - 存储每次上传的版本快照"""
    
    id = Column(String(36), primary_key=True)           # 版本ID (UUID)
    resume_id = Column(String(36), ForeignKey("resumes.id"))  # 关联简历ID
    version_number = Column(Integer, nullable=False)    # 版本号 (1, 2, 3...)
    file_name = Column(String(256), nullable=False)     # 文件名
    file_size = Column(Integer)                         # 文件大小(字节)
    raw_text = Column(Text)                             # 原始简历文本
    parsed_data = Column(JSON)                          # 解析后的结构化数据
    analysis_report = Column(Text)                      # AI分析报告(Markdown)
    analysis_metadata = Column(JSON)                    # 分析元数据(Token使用量等)
    upload_time = Column(DateTime, default=cst_now)     # 上传时间
    version_note = Column(String(512))                  # 版本说明
```

**关系定义**:
- 多对一：`resume` - 每个版本属于一个简历

**索引**:
- `idx_version_resume_id` - 按简历ID查询版本
- `idx_version_upload_time` - 按时间排序
- `idx_version_number` - 联合索引(resume_id + version_number)

**特点**:
- ✅ 每次上传自动创建新版本
- ✅ 版本号自动递增
- ✅ 保存完整的分析报告和原始数据
- ✅ 支持版本回溯和对比

---

### 3. ResumeSkill 表（简历技能关联）

**表名**: `resume_skills`

```python
class ResumeSkill(Base):
    """简历技能关联表 - 存储从简历中提取的技能"""
    
    id = Column(String(36), primary_key=True)           # 关联ID (UUID)
    resume_id = Column(String(36), ForeignKey("resumes.id"))  # 简历ID
    skill_id = Column(String(36), ForeignKey("skills.id"))    # 技能ID
    confidence = Column(Float, default=0.0)             # 置信度 (0.0-1.0)
    source_text = Column(Text)                          # 来源文本
    proficiency_level = Column(String(32))              # 熟练程度
    years_experience = Column(Integer)                  # 经验年限
    extracted_at = Column(DateTime, default=cst_now)    # 提取时间
```

**关系定义**:
- 多对一：`resume` - 每个技能关联属于一个简历
- 多对一：`skill` - 每个技能关联指向一个技能定义

**索引**:
- `idx_resume_skill_resume_id` - 按简历ID查询技能
- `idx_resume_skill_skill_id` - 按技能ID查询简历
- `idx_resume_skill_confidence` - 按置信度排序

---

## 数据访问层

### 1. ResumeVersionDAO - 版本管理数据访问对象

**文件**: `api/app/dao/resume_version_dao.py`

#### 核心方法

##### 1.1 create_version - 创建新版本

```python
def create_version(
    self,
    resume_id: str,
    file_name: str,
    file_size: int,
    raw_text: str,
    parsed_data: Dict[str, Any],
    analysis_report: str,
    analysis_metadata: Optional[Dict[str, Any]] = None,
    version_note: Optional[str] = None,
) -> ResumeVersion:
    """
    创建新的简历版本
    
    功能:
    - 自动获取当前最大版本号并递增
    - 保存完整的简历数据和分析报告
    - 记录上传时间和元数据
    
    返回: 创建的版本对象
    """
```

**实现逻辑**:
```python
# 1. 查询当前最大版本号
max_version = db.query(ResumeVersion.version_number)
    .filter(ResumeVersion.resume_id == resume_id)
    .order_by(desc(ResumeVersion.version_number))
    .first()

# 2. 计算下一个版本号
next_version = (max_version[0] + 1) if max_version else 1

# 3. 创建新版本记录
new_version = ResumeVersion(
    resume_id=resume_id,
    version_number=next_version,
    file_name=file_name,
    # ... 其他字段
)

# 4. 保存到数据库
db.add(new_version)
db.commit()
```

---

##### 1.2 get_versions_by_resume_id - 获取版本列表

```python
def get_versions_by_resume_id(
    self, 
    resume_id: str, 
    limit: int = 50
) -> List[ResumeVersion]:
    """
    获取指定简历的所有版本历史
    
    参数:
    - resume_id: 简历ID
    - limit: 返回数量限制
    
    返回: 版本列表（按上传时间倒序）
    """
```

**实现逻辑**:
```python
versions = db.query(ResumeVersion)
    .filter(ResumeVersion.resume_id == resume_id)
    .order_by(desc(ResumeVersion.upload_time))
    .limit(limit)
    .all()
```

---

##### 1.3 get_version_by_id - 获取版本详情

```python
def get_version_by_id(self, version_id: str) -> Optional[ResumeVersion]:
    """
    根据版本ID获取版本详情
    
    返回: 版本对象，如果不存在则返回None
    """
```

---

##### 1.4 get_latest_version - 获取最新版本

```python
def get_latest_version(self, resume_id: str) -> Optional[ResumeVersion]:
    """
    获取指定简历的最新版本
    
    返回: 最新版本对象
    """
```

**实现逻辑**:
```python
latest = db.query(ResumeVersion)
    .filter(ResumeVersion.resume_id == resume_id)
    .order_by(desc(ResumeVersion.version_number))
    .first()
```

---

##### 1.5 delete_version - 删除版本

```python
def delete_version(self, version_id: str) -> bool:
    """
    删除指定版本
    
    返回: 是否删除成功
    """
```

---

##### 1.6 get_or_create_resume - 获取或创建简历记录

```python
def get_or_create_resume(
    self,
    user_id: str,
    name: str,
    email: Optional[str] = None,
    phone: Optional[str] = None,
) -> Resume:
    """
    获取或创建简历记录（用于首次上传）
    
    逻辑:
    1. 尝试根据邮箱或姓名查找现有简历
    2. 如果不存在，创建新简历记录
    3. 确保简历关联到正确的用户
    
    返回: 简历对象
    """
```

**实现逻辑**:
```python
# 1. 先尝试通过邮箱查找
if email:
    resume = db.query(Resume)
        .filter(Resume.user_id == user_id, Resume.email == email)
        .first()

# 2. 如果没有，再通过姓名查找
if not resume and name:
    resume = db.query(Resume)
        .filter(Resume.user_id == user_id, Resume.name == name)
        .first()

# 3. 如果还是没有，创建新记录
if not resume:
    resume = Resume(
        user_id=user_id,
        name=name,
        email=email,
        phone=phone,
        parse_status="success"
    )
    db.add(resume)
    db.commit()
```

---

## 数据类型定义

### 文件: `api/app/types/resume_types.py`

#### 1. ContactInfo - 联系信息

```python
class ContactInfo(BaseModel):
    """联系信息"""
    email: Optional[str] = None
    phone: Optional[str] = None
    address: Optional[str] = None
    linkedin: Optional[str] = None
    github: Optional[str] = None
```

---

#### 2. Education - 教育经历

```python
class Education(BaseModel):
    """教育经历"""
    school: str = Field(..., description="学校名称")
    degree: str = Field(..., description="学位/专业")
    start: Optional[str] = Field(None, description="开始时间 (YYYY-MM 格式)")
    end: Optional[str] = Field(None, description="结束时间 (YYYY-MM 格式)")
    gpa: Optional[str] = None
    description: Optional[str] = None
```

---

#### 3. Experience - 工作经历

```python
class Experience(BaseModel):
    """工作经历"""
    company: str = Field(..., description="公司名称")
    title: str = Field(..., description="职位名称")
    start: Optional[str] = Field(None, description="开始时间 (YYYY-MM 格式)")
    end: Optional[str] = Field(None, description="结束时间 (YYYY-MM 格式)")
    desc: Optional[str] = Field(None, description="工作描述")
    skills: List[str] = Field(default_factory=list, description="相关技能")
```

---

#### 4. Skill - 技能信息

```python
class Skill(BaseModel):
    """技能信息"""
    name: str = Field(..., description="技能名称")
    confidence: float = Field(..., ge=0.0, le=1.0, description="掌握程度 (0-1)")
    category: Optional[str] = Field(None, description="技能类别")
    
    @validator("confidence")
    def validate_confidence(cls, v):
        if not (0.0 <= v <= 1.0):
            raise ValueError("Skill confidence must be between 0.0 and 1.0")
        return v
```

---

#### 5. ParsedResume - 解析后的简历数据

```python
class ParsedResume(BaseModel):
    """解析后的简历数据"""
    name: str = Field(..., description="姓名")
    contact: ContactInfo = Field(default_factory=ContactInfo)
    education: List[Education] = Field(default_factory=list)
    experience: List[Experience] = Field(default_factory=list)
    skills: List[Skill] = Field(default_factory=list)
    certificates: List[str] = Field(default_factory=list)
    raw_text: str = Field(..., description="原始文本内容")
```

**特点**:
- ✅ 使用Pydantic进行数据验证
- ✅ 自动类型转换和校验
- ✅ 支持JSON序列化/反序列化
- ✅ 提供默认值处理

---

#### 6. ResumeUploadResponse - 简历上传响应

```python
class ResumeUploadResponse(BaseModel):
    """简历上传响应"""
    success: bool
    message: str
    resume_id: Optional[str] = None
    parsed_data: Optional[ParsedResume] = None
    error_details: Optional[str] = None
```

---

#### 7. RESUME_PARSE_PROMPT - 简历解析提示词

这是用于指导LLM解析简历的系统提示词，定义了输出的JSON格式和字段要求。

```python
RESUME_PARSE_PROMPT = """你是一个高精度简历解析器。输入：候选人简历纯文本。
请仅返回合法 JSON，格式严格如下：

{
  "name": "姓名",
  "contact": {...},
  "education": [...],
  "experience": [...],
  "skills": [...],
  "certificates": [...],
  "raw_text": "原始简历文本"
}

如果信息不存在使用空字符串或空数组。confidence 值应该在 0.0-1.0 之间。
不要返回额外文字或解释。
"""
```

**用途**:
- 指导LLM按照统一格式输出
- 确保返回的JSON可以被Pydantic验证
- 提供示例和说明

---

## 业务服务层

### 1. ResumeParser - 简历解析服务

**文件**: `api/app/service/resume_parser.py`

#### 核心功能

##### 1.1 文件处理能力

```python
class ResumeParser:
    SUPPORTED_EXTENSIONS = {".pdf", ".docx", ".txt"}
    MAX_FILE_SIZE = 10 * 1024 * 1024  # 10MB
```

**支持的文件格式**:
- ✅ PDF (`.pdf`) - 使用 PyPDF2
- ✅ Word文档 (`.docx`) - 使用 python-docx
- ✅ 纯文本 (`.txt`) - 支持多种编码

---

##### 1.2 extract_text_from_file - 文本提取

```python
def extract_text_from_file(
    self, 
    file_content: bytes, 
    filename: str
) -> str:
    """
    从上传文件中提取文本内容
    
    处理流程:
    1. 验证文件大小（最大10MB）
    2. 检查文件格式是否支持
    3. 根据文件类型调用相应的提取方法
    4. 返回提取的文本内容
    
    异常处理:
    - ResumeParsingError: 文件格式不支持或提取失败
    """
```

**PDF提取逻辑**:
```python
def _extract_text_from_pdf(self, file_content: bytes) -> str:
    """提取PDF文本"""
    pdf_reader = PyPDF2.PdfReader(io.BytesIO(file_content))
    
    # 处理加密PDF
    if pdf_reader.is_encrypted:
        if not pdf_reader.decrypt(""):
            raise ResumeParsingError("PDF文件已加密，无法读取")
    
    # 逐页提取文本
    text_parts = []
    for page in pdf_reader.pages:
        page_text = page.extract_text()
        if page_text.strip():
            text_parts.append(page_text)
    
    # 检查是否提取到内容
    if not text_parts:
        raise ResumeParsingError("PDF中没有可读取的文本内容")
    
    return "\n".join(text_parts)
```

**DOCX提取逻辑**:
```python
def _extract_text_from_docx(self, file_content: bytes) -> str:
    """提取DOCX文本"""
    doc = docx.Document(io.BytesIO(file_content))
    text_parts = [paragraph.text for paragraph in doc.paragraphs]
    return "\n".join(text_parts)
```

**TXT提取逻辑**:
```python
def _extract_text_from_txt(self, file_content: bytes) -> str:
    """提取TXT文本，支持多种编码"""
    encodings = ["utf-8", "gbk", "gb2312", "latin-1"]
    
    for encoding in encodings:
        try:
            return file_content.decode(encoding)
        except UnicodeDecodeError:
            continue
    
    # 所有编码都失败，使用UTF-8并忽略错误
    return file_content.decode("utf-8", errors="replace")
```

---

##### 1.3 parse_resume_text - LLM解析

```python
def parse_resume_text(
    self, 
    text_content: str, 
    filename: Optional[str] = None
) -> ParsedResume:
    """
    使用LLM解析简历文本
    
    处理流程:
    1. 验证文本内容非空
    2. 构建LLM提示词
    3. 调用LLM API进行解析
    4. 解析JSON响应
    5. 验证数据格式
    6. 返回ParsedResume对象
    
    参数:
    - text_content: 简历文本内容
    - filename: 可选的文件名（用于日志）
    
    返回: ParsedResume对象
    
    异常:
    - ResumeParsingError: 解析失败或验证错误
    """
```

**实现逻辑**:
```python
# 1. 构建消息
messages = [
    {
        "role": "system",
        "content": "你是一个专业的简历解析器，请严格按照指定的JSON格式输出结果。"
    },
    {
        "role": "user",
        "content": RESUME_PARSE_PROMPT + text_content
    }
]

# 2. 调用LLM
response = self.llm_client.chat_completion(
    messages=messages,
    temperature=0.1,  # 低温度，确保输出稳定
    max_tokens=2000,
    response_format={"type": "json_object"}  # 强制JSON输出
)

# 3. 解析JSON
parsed_json = json.loads(response.content)

# 4. 验证并创建对象
parsed_resume = ParsedResume(**parsed_json)  # Pydantic自动验证

return parsed_resume
```

---

##### 1.4 process_uploaded_file - 完整处理流程

```python
def process_uploaded_file(
    self, 
    file_content: bytes, 
    filename: str,
    user_id: str
) -> Tuple[str, ParsedResume]:
    """
    完整的简历处理流程
    
    步骤:
    1. 从文件中提取文本
    2. 验证文本内容
    3. 使用LLM解析简历
    4. 生成唯一的简历ID
    
    返回: (resume_id, ParsedResume对象)
    """
```

**完整流程**:
```python
# 1. 提取文本
text_content = self.extract_text_from_file(file_content, filename)

# 2. 验证内容
if not text_content.strip():
    raise ResumeParsingError("文件中没有可提取的文本")

# 3. LLM解析
parsed_resume = self.parse_resume_text(text_content, filename)

# 4. 生成ID
resume_id = str(uuid.uuid4())

return resume_id, parsed_resume
```

---

### 2. ResumeAnalyzerService - 简历智能分析服务

**文件**: `api/app/service/resume_analyzer.py`

#### 核心功能

##### 2.1 analyze_resume - 生成智能分析报告

```python
def analyze_resume(
    self, 
    resume_text: str, 
    parsed_resume: Optional[ParsedResume] = None
) -> Dict[str, Any]:
    """
    分析简历并生成智能报告
    
    功能:
    - 深度分析候选人背景
    - 评估技能和经验
    - 提供职业发展建议
    - 生成Markdown格式报告
    
    参数:
    - resume_text: 简历原始文本
    - parsed_resume: 可选的已解析简历数据（用于更精准分析）
    
    返回:
    {
        "success": bool,
        "report_markdown": str,  # Markdown格式的报告
        "usage": dict,           # LLM使用统计
        "error": Optional[str]
    }
    """
```

**分析报告结构**:
```markdown
# 🧾 简历智能分析报告

## 一、候选人概况
- 核心信息总结
- 一句话概括核心竞争力

## 二、教育背景分析
- 学历层次及学校评价
- 专业匹配度分析
- 学业成绩评估
- 教育亮点总结

## 三、技能与能力画像
### 3.1 技术技能
- 技术栈及熟练程度
- 技能广度与深度评估
- 技术趋势匹配度

### 3.2 软技能
- 团队协作、沟通、领导力等

### 3.3 技能评级
⭐⭐⭐⭐⭐ (1-5星评分)

## 四、实践与项目经验
### 4.1 工作/实习经历
### 4.2 项目经验
### 4.3 竞赛与荣誉

## 五、职业发展建议
### 5.1 优势分析
### 5.2 待提升领域
### 5.3 职业方向建议
### 5.4 技能提升路径

## 六、综合评价
### 6.1 整体评分
- 技术能力: ⭐⭐⭐⭐⭐ (X/5)
- 项目经验: ⭐⭐⭐⭐⭐ (X/5)
- 发展潜力: ⭐⭐⭐⭐⭐ (X/5)
- 综合评分: ⭐⭐⭐⭐⭐ (X/5)

### 6.2 推荐指数
### 6.3 一句话总结
```

---

##### 2.2 _build_analysis_content - 构建分析内容

```python
def _build_analysis_content(
    self, 
    resume_text: str, 
    parsed_resume: Optional[ParsedResume] = None
) -> str:
    """
    构建用于分析的内容
    
    逻辑:
    - 如果有结构化数据，使用JSON格式提供更详细的信息
    - 如果只有原始文本，直接使用文本进行分析
    
    返回: 格式化的分析输入内容
    """
```

**带结构化数据的格式**:
```python
structured_data = {
    "基本信息": {
        "姓名": parsed_resume.name,
        "联系方式": parsed_resume.contact.dict()
    },
    "教育背景": [edu.dict() for edu in parsed_resume.education],
    "工作经历": [exp.dict() for exp in parsed_resume.experience],
    "技能列表": [skill.dict() for skill in parsed_resume.skills],
    "证书": parsed_resume.certificates
}

return f"""
原始简历文本：
{resume_text}

结构化数据（JSON格式）：
{json.dumps(structured_data, ensure_ascii=False, indent=2)}
"""
```

---

##### 2.3 LLM调用配置

```python
response = self.llm_client.chat_completion(
    messages=messages,
    temperature=0.7,      # 适中的创造性，允许一定的灵活性
    max_tokens=3000,      # 充足的token用于生成详细报告
)
```

**参数说明**:
- `temperature=0.7`: 平衡创造性和准确性
- `max_tokens=3000`: 确保报告内容完整
- 系统角色: "资深的人力资源专家和职业发展顾问"

---

### 3. SkillExtractor - 技能提取服务

**文件**: `api/app/service/skill_extractor.py`

#### 功能概述

技能提取器负责从简历中自动识别和提取技能，包括技术技能和软技能，并进行分类和置信度评估。

#### 核心方法

```python
def extract_skills_from_resume(
    self, 
    parsed_resume: ParsedResume
) -> ExtractionResult:
    """
    从简历中提取技能
    
    提取策略:
    1. 从skills字段直接提取
    2. 从experience中的skills列表提取
    3. 从工作描述中使用NLP提取
    4. 去重并计算置信度
    
    返回: ExtractionResult对象
    """
```

---

## API控制层

### 文件: `api/app/controller/resume_router.py`

#### API端点总览

| 端点 | 方法 | 功能 | 认证要求 |
|------|------|------|----------|
| `/api/resumes/upload` | POST | 上传并解析简历 | ✅ JWT |
| `/api/resumes/parse-text` | POST | 直接解析文本 | ❌ |
| `/api/resumes/analyze` | POST | 上传并智能分析 | ✅ JWT |
| `/api/resumes/versions` | GET | 获取版本列表 | ✅ JWT |
| `/api/resumes/versions/{id}` | GET | 获取版本详情 | ❌ |
| `/api/resumes/versions/{id}` | DELETE | 删除版本 | ❌ |
| `/api/resumes/health` | GET | 健康检查 | ❌ |

---

### 1. POST /api/resumes/upload - 上传并解析简历

```python
@router.post("/upload", response_model=ResumeUploadResponse)
async def upload_resume(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...),
    current_user: CurrentUser = Depends(get_current_user),
):
    """
    上传并解析简历文件，自动提取技能
    
    处理流程:
    1. 验证文件
    2. 读取文件内容
    3. 调用解析器处理文件
    4. 后台任务提取技能
    5. 返回解析结果
    
    特点:
    - ✅ 自动关联到当前用户
    - ✅ 后台异步提取技能
    - ✅ 返回结构化简历数据
    """
```

**完整实现**:
```python
# 1. 验证文件
if not file.filename:
    raise HTTPException(status_code=400, detail="No file provided")

# 2. 读取内容
file_content = await file.read()

# 3. 解析简历
parser = get_resume_parser()
resume_id, parsed_resume = parser.process_uploaded_file(
    file_content=file_content,
    filename=file.filename,
    user_id=current_user.user_id
)

# 4. 后台提取技能
background_tasks.add_task(
    extract_and_save_skills, 
    resume_id, 
    parsed_resume
)

# 5. 返回响应
return ResumeUploadResponse(
    success=True,
    message="简历上传并解析成功，正在后台提取技能",
    resume_id=resume_id,
    parsed_data=parsed_resume
)
```

---

### 2. POST /api/resumes/analyze - 上传并智能分析

这是**最核心的API**，实现了完整的简历分析和版本管理功能。

```python
@router.post("/analyze")
async def analyze_resume(
    file: UploadFile = File(...),
    current_user: CurrentUser = Depends(get_current_user),
):
    """
    上传简历并生成智能分析报告（自动保存版本历史）
    
    完整流程:
    1. 验证文件
    2. 提取文本
    3. 解析简历（获取结构化数据）
    4. LLM智能分析（生成Markdown报告）
    5. 保存版本到数据库
    6. 返回分析结果
    
    特点:
    - ✅ 自动创建/关联简历记录
    - ✅ 自动保存版本历史
    - ✅ 生成专业分析报告
    - ✅ 关联到当前用户
    """
```

**详细实现**:

```python
# 第一步：验证文件
if not file.filename:
    raise HTTPException(status_code=400, detail="未提供文件")

file_content = await file.read()
file_size = len(file_content)

# 第二步：提取文本
parser = get_resume_parser()
resume_text = parser.extract_text_from_file(
    file_content=file_content, 
    filename=file.filename
)

# 第三步：解析简历（获取结构化数据）
parsed_resume = None
try:
    parsed_resume = parser.parse_resume_text(resume_text, file.filename)
except Exception as e:
    logger.warning(f"简历解析失败，将只使用原始文本: {e}")

# 第四步：LLM智能分析
analyzer = get_resume_analyzer()
analysis_result = analyzer.analyze_resume(
    resume_text=resume_text,
    parsed_resume=parsed_resume
)

# 第五步：保存版本到数据库
version_dao = ResumeVersionDAO()

# 5.1 获取或创建简历记录
candidate_name = parsed_resume.name if parsed_resume else current_user.name
candidate_email = parsed_resume.contact.email if parsed_resume else None
candidate_phone = parsed_resume.contact.phone if parsed_resume else None

resume_record = version_dao.get_or_create_resume(
    user_id=current_user.user_id,  # 关联到当前用户
    name=candidate_name,
    email=candidate_email,
    phone=candidate_phone
)

# 5.2 创建新版本
new_version = version_dao.create_version(
    resume_id=resume_record.id,
    file_name=file.filename,
    file_size=file_size,
    raw_text=resume_text,
    parsed_data=parsed_resume.model_dump() if parsed_resume else {},
    analysis_report=analysis_result["report_markdown"],
    analysis_metadata={"usage": analysis_result["usage"]},
    version_note=f"自动上传 - {file.filename}"
)

# 第六步：返回分析结果
return success_response(
    message="简历分析完成",
    data={
        "report_markdown": analysis_result["report_markdown"],
        "usage": analysis_result["usage"],
        "filename": file.filename,
        "candidate_name": candidate_name,
        "version_id": new_version.id,
        "version_number": new_version.version_number,
        "resume_id": resume_record.id
    }
)
```

**错误处理**:
```python
except ResumeParsingError as e:
    return error_response(message="文件处理失败", data={"error": str(e)})

except ResumeAnalyzerError as e:
    return error_response(message="分析失败", data={"error": str(e)})

except Exception as e:
    logger.error(f"分析简历时发生意外错误: {e}", exc_info=True)
    return error_response(message="服务器内部错误")
```

---

### 3. GET /api/resumes/versions - 获取版本列表

```python
@router.get("/versions")
async def get_resume_versions(
    current_user: CurrentUser = Depends(get_current_user),
    limit: int = 50,
):
    """
    获取简历版本历史
    
    权限控制:
    - 普通用户只能看到自己上传的简历
    - 管理员可以看到所有用户的简历
    
    返回:
    - versions: 版本列表（按时间倒序）
    - total: 总数
    - is_admin: 是否为管理员
    """
```

**实现逻辑**:
```python
version_dao = ResumeVersionDAO()

with getDatabaseSession() as db:
    # 权限判断
    if current_user.is_admin():
        # 管理员查看所有简历
        user_resumes = db.query(Resume).all()
    else:
        # 普通用户只查看自己的
        user_resumes = db.query(Resume)
            .filter(Resume.user_id == current_user.user_id)
            .all()

# 获取所有版本
all_versions = []
for resume in user_resumes:
    versions = version_dao.get_versions_by_resume_id(resume.id, limit)
    for version in versions:
        all_versions.append({
            "version_id": version.id,
            "resume_id": version.resume_id,
            "version_number": version.version_number,
            "file_name": version.file_name,
            "file_size": version.file_size,
            "upload_time": version.upload_time.isoformat(),
            "candidate_name": resume.name,
            "user_id": resume.user_id,
            "has_analysis": bool(version.analysis_report)
        })

# 按时间倒序排序
all_versions.sort(key=lambda x: x["upload_time"], reverse=True)

return success_response(
    message="获取版本历史成功",
    data={
        "versions": all_versions[:limit],
        "total": len(all_versions),
        "is_admin": current_user.is_admin()
    }
)
```

---

### 4. GET /api/resumes/versions/{version_id} - 获取版本详情

```python
@router.get("/versions/{version_id}")
async def get_version_detail(version_id: str):
    """
    获取指定版本的详细信息
    
    返回:
    - 版本基本信息
    - 原始文本
    - 解析后的数据
    - 分析报告（Markdown）
    - 元数据
    """
```

**实现**:
```python
version_dao = ResumeVersionDAO()
version = version_dao.get_version_by_id(version_id)

if not version:
    raise HTTPException(status_code=404, detail="版本不存在")

return success_response(
    message="获取版本详情成功",
    data={
        "version_id": version.id,
        "resume_id": version.resume_id,
        "version_number": version.version_number,
        "file_name": version.file_name,
        "file_size": version.file_size,
        "raw_text": version.raw_text,
        "parsed_data": version.parsed_data,
        "analysis_report": version.analysis_report,  # Markdown报告
        "analysis_metadata": version.analysis_metadata,
        "upload_time": version.upload_time.isoformat(),
        "version_note": version.version_note
    }
)
```

---

### 5. DELETE /api/resumes/versions/{version_id} - 删除版本

```python
@router.delete("/versions/{version_id}")
async def delete_version(version_id: str):
    """
    删除指定版本
    
    操作:
    - 从数据库中删除版本记录
    - 不影响其他版本
    - 不会删除主简历记录
    """
```

**实现**:
```python
version_dao = ResumeVersionDAO()
success = version_dao.delete_version(version_id)

if not success:
    raise HTTPException(status_code=404, detail="版本不存在")

return success_response(message="删除版本成功")
```

---

### 6. 后台任务：extract_and_save_skills

```python
def extract_and_save_skills(
    resume_id: str, 
    parsed_resume: ParsedResume
):
    """
    后台任务：提取技能并保存到数据库
    
    流程:
    1. 获取技能提取器
    2. 从简历中提取技能
    3. 转换为DAO对象
    4. 批量保存到数据库
    
    特点:
    - 异步执行，不阻塞主流程
    - 错误不影响简历上传成功
    """
```

**实现**:
```python
try:
    # 1. 提取技能
    skill_extractor = get_skill_extractor()
    extraction_result = skill_extractor.extract_skills_from_resume(parsed_resume)
    
    # 2. 转换为DAO对象
    resume_skill_dao = ResumeSkillDAO()
    dao_skills = [
        DAOExtractedSkill(
            name=skill.name,
            confidence=skill.confidence,
            source_text=skill.source_text,
            category=skill.category,
            proficiency_level=skill.proficiency_level,
            years_experience=skill.years_experience
        )
        for skill in extraction_result.extracted_skills
    ]
    
    # 3. 批量保存
    if dao_skills:
        resume_skill_dao.batch_create_resume_skills(resume_id, dao_skills)
    
    logger.info(f"提取并保存了 {len(dao_skills)} 个技能")
    
except Exception as e:
    logger.error(f"技能提取失败: {e}")
    # 不抛出异常，避免影响主流程
```

---

## 核心功能流程

### 流程1: 简历上传与解析

```
用户上传文件
    │
    ↓
[1] API接收文件 (resume_router.upload_resume)
    │
    ├─→ 验证文件格式和大小
    ├─→ 读取文件内容
    │
    ↓
[2] 文本提取 (ResumeParser.extract_text_from_file)
    │
    ├─→ PDF: PyPDF2
    ├─→ DOCX: python-docx
    ├─→ TXT: 多编码尝试
    │
    ↓
[3] LLM解析 (ResumeParser.parse_resume_text)
    │
    ├─→ 构建提示词
    ├─→ 调用LLM API
    ├─→ 解析JSON响应
    ├─→ Pydantic验证
    │
    ↓
[4] 后台任务 (extract_and_save_skills)
    │
    ├─→ 提取技能
    ├─→ 保存到数据库
    │
    ↓
返回解析结果给用户
```

---

### 流程2: 简历智能分析（带版本管理）

```
用户上传文件并请求分析
    │
    ↓
[1] API接收 (resume_router.analyze_resume)
    │
    ├─→ 验证文件
    ├─→ 读取内容
    │
    ↓
[2] 文本提取 (ResumeParser.extract_text_from_file)
    │
    ↓
[3] 简历解析 (ResumeParser.parse_resume_text)
    │  (获取结构化数据，用于更精准的分析)
    │
    ↓
[4] LLM智能分析 (ResumeAnalyzerService.analyze_resume)
    │
    ├─→ 构建分析提示词
    ├─→ 包含原始文本 + 结构化数据
    ├─→ 调用LLM生成Markdown报告
    │
    ↓
[5] 获取或创建简历记录 (ResumeVersionDAO.get_or_create_resume)
    │
    ├─→ 尝试通过邮箱查找
    ├─→ 尝试通过姓名查找
    ├─→ 不存在则创建新记录
    ├─→ 关联到当前用户
    │
    ↓
[6] 创建版本记录 (ResumeVersionDAO.create_version)
    │
    ├─→ 获取当前最大版本号
    ├─→ 版本号 +1
    ├─→ 保存文件信息
    ├─→ 保存原始文本
    ├─→ 保存解析数据
    ├─→ 保存分析报告
    ├─→ 保存元数据（Token使用量）
    │
    ↓
返回分析报告 + 版本信息
```

---

### 流程3: 版本历史管理

```
用户请求查看版本历史
    │
    ↓
[1] API接收 (resume_router.get_resume_versions)
    │
    ├─→ 验证用户身份
    ├─→ 判断权限（普通用户/管理员）
    │
    ↓
[2] 查询用户的简历记录
    │
    ├─→ 管理员：查询所有简历
    ├─→ 普通用户：只查询自己的
    │
    ↓
[3] 遍历每个简历，获取版本列表
    │  (ResumeVersionDAO.get_versions_by_resume_id)
    │
    ├─→ 按resume_id查询
    ├─→ 按upload_time倒序
    ├─→ 限制返回数量
    │
    ↓
[4] 汇总所有版本
    │
    ├─→ 合并到一个列表
    ├─→ 按时间全局排序
    ├─→ 添加候选人姓名等信息
    │
    ↓
返回版本列表给前端
```

---

### 流程4: 查看特定版本

```
用户点击查看某个版本
    │
    ↓
[1] API接收 (resume_router.get_version_detail)
    │
    ├─→ 接收version_id
    │
    ↓
[2] 查询版本详情 (ResumeVersionDAO.get_version_by_id)
    │
    ├─→ 根据version_id查询
    ├─→ 返回完整数据
    │
    ↓
[3] 返回版本详情
    │
    ├─→ 基本信息（文件名、大小、时间）
    ├─→ 原始文本
    ├─→ 解析后的数据（JSON）
    ├─→ 分析报告（Markdown）
    ├─→ 元数据（Token使用量）
    │
    ↓
前端展示版本内容
    │
    ├─→ 渲染Markdown报告
    ├─→ 显示结构化数据
    ├─→ 提供下载选项
```

---

### 流程5: 删除版本

```
用户请求删除某个版本
    │
    ↓
[1] 前端确认提示
    │  "确定要删除此版本吗？"
    │
    ↓
[2] API接收 (resume_router.delete_version)
    │
    ├─→ 接收version_id
    │
    ↓
[3] 删除版本 (ResumeVersionDAO.delete_version)
    │
    ├─→ 查找版本记录
    ├─→ 删除记录
    ├─→ 提交事务
    │
    ↓
[4] 返回删除结果
    │
    ↓
前端刷新版本列表
```

---

## 完整代码清单

### 文件结构

```
api/app/
├── controller/
│   └── resume_router.py          # API路由和端点定义
├── service/
│   ├── resume_parser.py          # 简历解析服务
│   ├── resume_analyzer.py        # 简历智能分析服务
│   ├── resume_parser_service.py  # 简历解析辅助服务
│   ├── resume_optimizer.py       # 简历优化服务（可选）
│   └── skill_extractor.py        # 技能提取服务
├── dao/
│   ├── resume_version_dao.py     # 版本管理DAO
│   └── skill_dao.py               # 技能DAO
├── dao/models/
│   └── models.py                  # 数据库模型定义
│       ├── Resume               # 简历表
│       ├── ResumeVersion        # 版本表
│       └── ResumeSkill          # 技能关联表
├── types/
│   └── resume_types.py           # 数据类型定义
│       ├── ContactInfo
│       ├── Education
│       ├── Experience
│       ├── Skill
│       ├── ParsedResume
│       ├── ResumeUploadResponse
│       └── RESUME_PARSE_PROMPT
└── utils/
    ├── llm_client.py              # LLM客户端
    └── http_resp.py               # HTTP响应工具
```

---

### 代码统计

| 模块 | 文件 | 行数 | 主要功能 |
|------|------|------|----------|
| **Controller** | resume_router.py | 444 | API端点，7个路由 |
| **Service** | resume_parser.py | 300+ | 文件解析，LLM调用 |
| **Service** | resume_analyzer.py | 257 | 智能分析，报告生成 |
| **DAO** | resume_version_dao.py | 200+ | 6个核心数据操作方法 |
| **Models** | models.py (部分) | 75+ | 3个数据库表定义 |
| **Types** | resume_types.py | 200+ | 7个Pydantic模型 |
| **总计** | - | **~1500行** | 完整的简历管理系统 |

---

### 核心依赖

```python
# 文件处理
PyPDF2           # PDF解析
python-docx      # DOCX解析

# Web框架
FastAPI          # API框架
Pydantic         # 数据验证

# 数据库
SQLAlchemy       # ORM
MySQL            # 数据库

# LLM
OpenAI / Azure OpenAI  # 智能分析

# 后台任务
BackgroundTasks  # FastAPI异步任务
```

---

### 配置项

```yaml
# JWT白名单 (无需认证的路径)
JWT_NO_CHECK_URIS: /,/apidoc,/resumes,/health,/doc,/stats

# 文件上传限制
MAX_FILE_SIZE: 10MB
SUPPORTED_FORMATS: PDF, DOCX, TXT

# LLM配置
OPENAI_API_KEY: xxx
OPENAI_MODEL: gpt-4
TEMPERATURE: 0.1 (解析) / 0.7 (分析)
MAX_TOKENS: 2000 (解析) / 3000 (分析)
```

---

## 总结

### 系统特点

1. **完整的分层架构**
   - 清晰的职责分离
   - 易于维护和扩展

2. **自动版本管理**
   - 每次上传自动保存版本
   - 版本号自动递增
   - 保存完整的分析历史

3. **智能分析能力**
   - LLM驱动的简历解析
   - 专业的职业发展建议
   - Markdown格式的报告

4. **灵活的权限控制**
   - 用户只能查看自己的简历
   - 管理员可以查看所有简历
   - 细粒度的访问控制

5. **异步处理**
   - 后台任务提取技能
   - 不阻塞主流程
   - 提升用户体验

6. **健壮的错误处理**
   - 多层次的异常捕获
   - 友好的错误提示
   - 不影响用户体验

### 技术亮点

- ✅ **三段式处理流程**: 文本提取 → LLM解析 → 智能分析
- ✅ **版本快照机制**: 完整保存每次上传的数据和报告
- ✅ **结构化存储**: JSON格式保存解析数据，便于查询和展示
- ✅ **Markdown报告**: 专业、可读性强的分析报告
- ✅ **多格式支持**: PDF、DOCX、TXT全覆盖
- ✅ **异步任务**: 技能提取不阻塞主流程

---

**文档结束**
